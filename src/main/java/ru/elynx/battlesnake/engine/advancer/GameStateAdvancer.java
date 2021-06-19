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

    private static GameState assemble(GameState gameState, int turn, List<Snake> snakes) {
        Snake you = findYouSnake(gameState, snakes);

        Board board = new Board(gameState.getBoard().getDimensions(), gameState.getBoard().getFood(),
                gameState.getBoard().getHazards(), snakes);

        return new GameState(gameState.getGameId(), turn, gameState.getRules(), board, you);
    }

    private static Snake findYouSnake(GameState gameState, List<Snake> snakes) {
        Snake you = snakes.stream().filter(someSnake -> someSnake.getId().equals(gameState.getYou().getId())).findAny()
                .orElse(null);

        if (you == null) {
            throw new IllegalStateException("You was not found");
        }

        return you;
    }

    private static int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private static List<Snake> makeSnakes(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        List<Snake> snakes = new ArrayList<>(gameState.getBoard().getSnakes().size());

        for (Snake snake : gameState.getBoard().getSnakes()) {
            MoveCommand moveCommand = moveDecider.apply(snake, gameState);
            snakes.add(makeSnake(gameState, snake, moveCommand));
        }

        return snakes;
    }

    private static Snake makeSnake(GameState gameState, Snake snake, MoveCommand moveCommand) {
        List<Coordinates> body = makeSnakeBody(gameState, snake, moveCommand);
        int health = makeHealth(snake);

        return new Snake(snake.getId(), snake.getName(), health, body, snake.getLatency(), body.get(0), body.size(),
                snake.getShout(), snake.getSquad());
    }

    private static List<Coordinates> makeSnakeBody(GameState gameState, Snake snake, MoveCommand moveCommand) {
        Coordinates nextHead = makeSnakeHead(snake, moveCommand);

        List<Coordinates> body = new ArrayList<>(snake.getBody().size());
        body.add(nextHead);

        int growthOffset = gameState.isSnakeGrowing(snake) ? 0 : 1;

        for (int i = 0; i < snake.getBody().size() - growthOffset; ++i) {
            body.add(snake.getBody().get(i));
        }

        return body;
    }

    private static Coordinates makeSnakeHead(Snake snake, MoveCommand moveCommand) {
        Coordinates nextHead = snake.getHead().sideNeighbours().stream()
                .filter((CoordinatesWithDirection coordinates) -> moveCommand.equals(coordinates.getDirection()))
                .findAny().orElse(null);

        if (nextHead == null) {
            throw new IllegalStateException("Could not find next head position for [" + snake.getId()
                    + "] and move command [" + moveCommand + ']');
        }

        return nextHead;
    }

    private static int makeHealth(Snake snake) {
        return snake.getHealth() - 1;
    }
}
