package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.javatuples.Pair;
import ru.elynx.battlesnake.entity.*;

@UtilityClass
public class GameStateAdvancer {
    public GameState advance(BiFunction<Snake, GameState, MoveCommand> moveDecisionMaker, GameState gameState) {
        BiFunction<Snake, GameState, Collection<MoveCommandAndProbability>> adapter = (Snake snake1,
                GameState gameState1) -> List
                        .of(new MoveCommandAndProbability(moveDecisionMaker.apply(snake1, gameState1), 1.0d));
        return advance(adapter, gameState.getYou(), gameState).map(Pair::getValue0).findAny().orElseThrow();
    }

    public Stream<Pair<GameState, Double>> advance(
            BiFunction<Snake, GameState, Collection<MoveCommandAndProbability>> moveDecisionMaker, Snake you,
            GameState gameState) {
        var turn = makeTurn(gameState);
        var snakes = makeSnakes(moveDecisionMaker, gameState);

        return snakes.stream().map(x -> new Pair<>(assemble(gameState, turn, x.getValue0(), you), x.getValue1()));
    }

    private int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private List<Pair<List<Snake>, Double>> makeSnakes(
            BiFunction<Snake, GameState, Collection<MoveCommandAndProbability>> moveDecisionMaker,
            GameState gameState) {
        var allSnakes = new ArrayList<List<Pair<Snake, Double>>>(gameState.getBoard().getSnakes().size());

        for (Snake currentSnake : gameState.getBoard().getSnakes()) {
            var moveCommands = moveDecisionMaker.apply(currentSnake, gameState);
            var singleSnake = new ArrayList<Pair<Snake, Double>>(moveCommands.size());

            for (var moveCommand : moveCommands) {
                var futureSnake = makeSnake(moveCommand.getMoveCommand(), currentSnake, gameState);
                if (futureSnake != null) {
                    singleSnake.add(new Pair<>(futureSnake, moveCommand.getProbability()));
                }
            }

            if (!singleSnake.isEmpty()) {
                allSnakes.add(singleSnake);
            }
        }

        return CartesianProduct.make(allSnakes);
    }

    private Snake makeSnake(MoveCommand moveCommand, Snake snake, GameState gameState) {
        Coordinates head = makeHead(moveCommand, snake);
        if (gameState.getBoard().getDimensions().isOutOfBounds(head)) {
            return null;
        }

        int health = makeHealth(head, snake, gameState);
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

    private int makeHealth(Coordinates head, Snake snake, GameState gameState) {
        if (isFood(head, gameState)) {
            return Snake.getMaxHealth();
        }

        int decrement = 1;

        if (isHazard(head, gameState)) {
            decrement += gameState.getRules().getHazardDamage();
        }

        return snake.getHealth() - decrement;
    }

    private boolean isFood(Coordinates coordinates, GameState gameState) {
        return gameState.getBoard().getFood().contains(coordinates);
    }

    private boolean isHazard(Coordinates coordinates, GameState gameState) {
        return gameState.getBoard().getActiveHazards().contains(coordinates);
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

    private Snake findYouSnake(Snake target, List<Snake> snakes) {
        // this is what API actually does
        // losing end `game state` has `you` defined, but absent from `board.snakes`
        return snakes.stream().filter(someSnake -> someSnake.getId().equals(target.getId())).findAny().orElse(target);
    }
}
