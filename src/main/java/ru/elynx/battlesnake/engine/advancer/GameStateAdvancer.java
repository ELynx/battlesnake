package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import ru.elynx.battlesnake.entity.*;

public class GameStateAdvancer {
    private GameStateAdvancer() {
    }

    public static GameState advance(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        int turn = makeTurn(gameState);
        List<Snake> snakes = makeSnakes(gameState, moveDecider);

        return assemble(gameState, turn, snakes);
    }

    private static int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private static List<Snake> makeSnakes(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
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

    private static Snake makeSnake(GameState gameState, Snake snake, MoveCommand moveCommand) {
        Coordinates head = makeHead(snake, moveCommand);
        if (gameState.getBoard().getDimensions().outOfBounds(head)) {
            return null; // eliminate by out of bounds
        }

        int health = makeHealth(gameState, snake, head);
        if (health <= 0) {
            return null; // eliminate by health
        }

        List<Coordinates> body = makeBody(gameState, snake, head);

        return new Snake(snake.getId(), snake.getName(), health, body, snake.getLatency(), head, body.size(),
                snake.getShout(), snake.getSquad());
    }

    private static Coordinates makeHead(Snake snake, MoveCommand moveCommand) {
        Coordinates nextHead = snake.getHead().sideNeighbours().stream()
                .filter((CoordinatesWithDirection coordinates) -> moveCommand.equals(coordinates.getDirection()))
                .findAny().orElse(null);

        if (nextHead == null) {
            throw new IllegalStateException("Could not find next head position for [" + snake.getId()
                    + "] and move command [" + moveCommand + ']');
        }

        return nextHead;
    }

    private static int makeHealth(GameState gameState, Snake snake, Coordinates head) {
        if (isFood(gameState, head)) {
            return Snake.getMaxHealth();
        }

        return snake.getHealth() - 1;
    }

    private static boolean isFood(GameState gameState, Coordinates coordinates) {
        return gameState.getBoard().getFood().contains(coordinates);
    }

    private static List<Coordinates> makeBody(GameState gameState, Snake snake, Coordinates head) {
        // TODO remove check duplicated by makeHealth
        boolean isGrowing = isFood(gameState, head);

        List<Coordinates> body = new ArrayList<>(snake.getBody().size() + (isGrowing ? 1 : 0));
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

    private static GameState assemble(GameState gameState, int turn, List<Snake> snakes) {
        // TODO improve checking, consumed food is actually known
        List<Coordinates> food = filterConsumedFood(gameState, snakes);
        snakes = eliminateSnakesByCollision(snakes);

        Board board = new Board(gameState.getBoard().getDimensions(), food, gameState.getBoard().getHazards(), snakes);

        Snake you = findYouSnake(gameState, snakes);
        return new GameState(gameState.getGameId(), turn, gameState.getRules(), board, you);
    }

    private static List<Coordinates> filterConsumedFood(GameState gameState, List<Snake> snakes) {
        List<Coordinates> food = new ArrayList<>(gameState.getBoard().getFood().size());

        for (Coordinates potentialFood : gameState.getBoard().getFood()) {
            boolean consumed = false;
            for (Snake snake : snakes) {
                if (potentialFood.equals(snake.getHead())) {
                    consumed = true;
                    break;
                }
            }

            if (!consumed) {
                food.add(potentialFood);
            }
        }

        return food;
    }

    private static List<Snake> eliminateSnakesByCollision(List<Snake> snakes) {
        List<Snake> result = new ArrayList<>(snakes.size());

        // TODO speed up by using snake lengths
        for (Snake checked : snakes) {
            boolean survived = true;
            for (Snake other : snakes) {
                if (isCollision(checked, other)) {
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

    private static boolean isCollision(Snake checked, Snake other) {
        return false;
    }

    private static Snake findYouSnake(GameState gameState, List<Snake> snakes) {
        // this is what API actually does
        // losing end `game state` has `you` defined, but absent from `board.snakes`
        return snakes.stream().filter(someSnake -> someSnake.getId().equals(gameState.getYou().getId())).findAny()
                .orElse(gameState.getYou());
    }
}
