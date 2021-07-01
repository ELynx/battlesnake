package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import lombok.experimental.UtilityClass;
import ru.elynx.battlesnake.entity.*;

@UtilityClass
public class GameStateAdvancer {
    public GameState advance(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        int turn = makeTurn(gameState);
        List<Snake> snakes = makeSnakes(gameState, moveDecider);

        return assemble(gameState, turn, snakes);
    }

    private int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private List<Snake> makeSnakes(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        List<Snake> snakes = new ArrayList<>(gameState.getBoard().getSnakes().size());

        for (Snake current : gameState.getBoard().getSnakes()) {
            MoveCommand moveCommand = moveDecider.apply(current, gameState);
            Snake future = makeSnake(gameState, current, moveCommand);
            if (future != null) {
                snakes.add(future);
            }
        }

        return snakes;
    }

    private Snake makeSnake(GameState gameState, Snake snake, MoveCommand moveCommand) {
        Coordinates head = makeHead(snake, moveCommand);
        if (gameState.getBoard().getDimensions().isOutOfBounds(head)) {
            return null;
        }

        int health = makeStandardHealth(gameState, snake, head);
        if (health <= 0) {
            return null;
        }

        List<Coordinates> body = makeBody(snake, head, health);

        return new Snake(snake.getId(), snake.getName(), health, body, snake.getLatency(), head, body.size(),
                snake.getShout(), snake.getSquad());
    }

    private Coordinates makeHead(Snake snake, MoveCommand moveCommand) {
        return snake.getHead().move(moveCommand);
    }

    private int makeStandardHealth(GameState gameState, Snake snake, Coordinates head) {
        if (isFood(gameState, head)) {
            return Snake.getMaxHealth();
        }

        return snake.getHealth() - 1;
    }

    private boolean isFood(GameState gameState, Coordinates coordinates) {
        return gameState.getBoard().getFood().contains(coordinates);
    }

    private List<Coordinates> makeBody(Snake snake, Coordinates head, int health) {
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

    private GameState assemble(GameState gameState, int turn, List<Snake> snakes) {
        // at this point `snakes` have all of heads moved
        List<Coordinates> food = findRemainingFood(gameState.getBoard().getFood(), snakes);
        snakes = eliminateSnakesByCollision(snakes);

        if (gameState.getRules().isRoyale()) {
            snakes = applyHazards(gameState, snakes);
        }

        Board board = new Board(gameState.getBoard().getDimensions(), food, gameState.getBoard().getHazards(), snakes);

        Snake you = findYouSnake(gameState, snakes);
        return new GameState(gameState.getGameId(), turn, gameState.getRules(), board, you);
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

    private List<Snake> applyHazards(GameState gameState, List<Snake> snakes) {
        List<Snake> result = new ArrayList<>(snakes.size());

        for (Snake current : snakes) {
            Snake future = applyHazards(gameState, current);
            if (future != null) {
                result.add(future);
            }
        }

        return result;
    }

    private Snake applyHazards(GameState gameState, Snake snake) {
        int health = makeRoyaleHealth(gameState, snake);
        if (health <= 0) {
            return null;
        }

        return snake.withHealth(health);
    }

    private int makeRoyaleHealth(GameState gameState, Snake snake) {
        if (gameState.getBoard().getActiveHazards().contains(snake.getHead())) {
            return snake.getHealth() - gameState.getRules().getRoyaleHazardDamage();
        }

        return snake.getHealth();
    }

    private Snake findYouSnake(GameState gameState, List<Snake> snakes) {
        // this is what API actually does
        // losing end `game state` has `you` defined, but absent from `board.snakes`
        return snakes.stream().filter(someSnake -> someSnake.getId().equals(gameState.getYou().getId())).findAny()
                .orElse(gameState.getYou());
    }
}
