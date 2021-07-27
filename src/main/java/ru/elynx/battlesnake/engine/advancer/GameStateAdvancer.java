package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.javatuples.Pair;
import ru.elynx.battlesnake.entity.*;

@UtilityClass
public class GameStateAdvancer {
    public GameState advance(BiFunction<Snake, GameState, MoveCommand> moveDecisionMaker, GameState gameState) {
        BiFunction<Snake, GameState, List<MoveCommandWithProbability>> adapter = (Snake snake1,
                GameState gameState1) -> List
                        .of(new MoveCommandWithProbability(moveDecisionMaker.apply(snake1, gameState1), 1.0d));
        return advance(adapter, gameState.getYou(), gameState).map(Pair::getValue0).findAny().orElseThrow();
    }

    public Stream<Pair<GameState, Double>> advance(
            BiFunction<Snake, GameState, List<MoveCommandWithProbability>> moveDecisionMaker, Snake you,
            GameState gameState) {
        int turn = makeTurn(gameState);
        List<Pair<List<Snake>, Double>> snakes = makeSnakes(moveDecisionMaker, gameState);

        return snakes.stream().map(x -> new Pair<>(assemble(gameState, turn, x.getValue0(), you), x.getValue1()));
    }

    private int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private List<Pair<List<Snake>, Double>> makeSnakes(
            BiFunction<Snake, GameState, List<MoveCommandWithProbability>> moveDecisionMaker, GameState gameState) {
        List<List<Pair<Snake, Double>>> allSnakes = new ArrayList<>(gameState.getBoard().getSnakes().size());

        for (Snake current : gameState.getBoard().getSnakes()) {
            var moveCommands = moveDecisionMaker.apply(current, gameState);
            List<Pair<Snake, Double>> singleSnake = new ArrayList<>(moveCommands.size());

            for (MoveCommandWithProbability moveCommand : moveCommands) {
                Snake future = makeSnake(moveCommand.getMoveCommand(), current, gameState);
                if (future != null) {
                    singleSnake.add(new Pair<>(future, moveCommand.getProbability()));
                }
            }

            if (!singleSnake.isEmpty()) {
                allSnakes.add(singleSnake);
            }
        }

        return cartesianProduct(allSnakes);
    }

    private List<Pair<List<Snake>, Double>> cartesianProduct(List<List<Pair<Snake, Double>>> allSnakes) {
        if (allSnakes.isEmpty()) {
            // there is 100% chance no snake is found
            return List.of(new Pair<>(Collections.emptyList(), 1.0d));
        }

        List<Pair<List<Snake>, Double>> result = new ArrayList<>();

        // start copy from stack overflow
        // https://stackoverflow.com/a/9591777/15529473
        int solutions = 1;

        for (List<Pair<Snake, Double>> singleSnake : allSnakes) {
            solutions *= singleSnake.size();
        }

        for (int i = 0; i < solutions; i++) {
            int j = 1;

            List<Snake> solution = new ArrayList<>(allSnakes.size());
            double probability = 1.0d;

            for (var singleSnake : allSnakes) {
                int index = (i / j) % singleSnake.size();
                var snake = singleSnake.get(index);

                solution.add(snake.getValue0());
                probability *= snake.getValue1();

                j *= singleSnake.size();
            }

            result.add(new Pair<>(solution, probability));
        }
        // end copy from stack overflow

        return result;
    }

    private Snake makeSnake(MoveCommand moveCommand, Snake snake, GameState gameState) {
        Coordinates head = makeHead(moveCommand, snake);
        if (gameState.getBoard().getDimensions().isOutOfBounds(head)) {
            return null;
        }

        int health = makeStandardHealth(head, snake, gameState);
        if (health <= 0) {
            return null;
        }

        List<Coordinates> body = makeBody(health, head, snake);

        return new Snake(snake.getId(), snake.getName(), health, body, snake.getLatency(), head, body.size(),
                snake.getShout(), snake.getSquad());
    }

    private Coordinates makeHead(MoveCommand moveCommand, Snake snake) {
        return snake.getHead().move(moveCommand);
    }

    private int makeStandardHealth(Coordinates head, Snake snake, GameState gameState) {
        if (isFood(head, gameState)) {
            return Snake.getMaxHealth();
        }

        return snake.getHealth() - 1;
    }

    private boolean isFood(Coordinates coordinates, GameState gameState) {
        return gameState.getBoard().getFood().contains(coordinates);
    }

    private List<Coordinates> makeBody(int health, Coordinates head, Snake snake) {
        boolean isGrowing = health == Snake.getMaxHealth();

        int bodyCapacity = snake.getBody().size() + (isGrowing ? 1 : 0);
        List<Coordinates> body = new ArrayList<>(bodyCapacity);

        body.add(head);

        // last piece always moves out
        for (int i = 0; i < snake.getBody().size() - 1; ++i) {
            body.add(snake.getBody().get(i));
        }

        if (isGrowing) {
            body.add(body.get(body.size() - 1));
        }

        return body;
    }

    private GameState assemble(GameState gameState, int turn, List<Snake> snakes, Snake you) {
        // at this point `snakes` have all of heads moved
        List<Coordinates> food = findRemainingFood(gameState.getBoard().getFood(), snakes);
        snakes = eliminateSnakesByCollision(snakes);

        if (gameState.getRules().isRoyale()) {
            snakes = applyHazards(snakes, gameState);
        }

        Board board = new Board(gameState.getBoard().getDimensions(), food, gameState.getBoard().getHazards(), snakes);

        Snake nextYou = findYouSnake(you, snakes);
        return new GameState(gameState.getGameId(), turn, gameState.getRules(), board, nextYou);
    }

    private List<Coordinates> findRemainingFood(List<Coordinates> foodBefore, List<Snake> snakes) {
        List<Coordinates> foodAfter = new ArrayList<>(foodBefore.size());

        for (Coordinates food : foodBefore) {
            boolean consumed = false;
            for (Snake snake : snakes) {
                if (food.equals(snake.getHead())) {
                    consumed = true;
                    break;
                }
            }

            if (!consumed) {
                foodAfter.add(food);
            }
        }

        return foodAfter;
    }

    private List<Snake> eliminateSnakesByCollision(List<Snake> snakes) {
        List<Snake> result = new ArrayList<>(snakes.size());

        for (Snake checked : snakes) {
            boolean survived = true;
            for (Snake other : snakes) {
                if (isCollisionLoss(checked, other)) {
                    survived = false;
                    break;
                }
            }

            if (survived) {
                result.add(checked);
            }
        }

        return result;
    }

    private boolean isCollisionLoss(Snake checked, Snake other) {
        List<Coordinates> otherBody = other.getBody();
        for (int i = 0; i < otherBody.size(); ++i) {
            if (otherBody.get(i).equals(checked.getHead())) {
                if (i == 0) {
                    if (isHeadToHeadLoss(checked, other)) {
                        return true;
                    }
                } else {
                    return true; // head to any part of body is always loss
                }
            }
        }

        return false;
    }

    private boolean isHeadToHeadLoss(Snake checked, Snake other) {
        return !other.getId().equals(checked.getId()) && other.getLength() >= checked.getLength();
    }

    private List<Snake> applyHazards(List<Snake> snakes, GameState gameState) {
        List<Snake> result = new ArrayList<>(snakes.size());

        for (Snake current : snakes) {
            Snake future = applyHazards(current, gameState);
            if (future != null) {
                result.add(future);
            }
        }

        return result;
    }

    private Snake applyHazards(Snake snake, GameState gameState) {
        int health = makeRoyaleHealth(snake, gameState);
        if (health <= 0) {
            return null;
        }

        return snake.withHealth(health);
    }

    private int makeRoyaleHealth(Snake snake, GameState gameState) {
        if (gameState.getBoard().getActiveHazards().contains(snake.getHead())) {
            return snake.getHealth() - gameState.getRules().getRoyaleHazardDamage();
        }

        return snake.getHealth();
    }

    private Snake findYouSnake(Snake target, List<Snake> snakes) {
        // this is what API actually does
        // losing end `game state` has `you` defined, but absent from `board.snakes`
        return snakes.stream().filter(someSnake -> someSnake.getId().equals(target.getId())).findAny().orElse(target);
    }
}
