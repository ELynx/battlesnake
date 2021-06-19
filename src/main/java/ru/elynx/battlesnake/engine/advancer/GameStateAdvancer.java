package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.Collections;
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
        int health = makeHealth(gameState, snake, head);
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
        Snake you = findYouSnake(gameState, snakes);

        Board board = new Board(gameState.getBoard().getDimensions(), Collections.emptyList(),
                gameState.getBoard().getHazards(), snakes);

        return new GameState(gameState.getGameId(), turn, gameState.getRules(), board, you);
    }

    private static Snake findYouSnake(GameState gameState, List<Snake> snakes) {
        // this is what API actually does
        // losing end `game state` has `you` defined, but absent from `board.snakes`
        return snakes.stream().filter(someSnake -> someSnake.getId().equals(gameState.getYou().getId())).findAny()
                .orElse(gameState.getYou());
    }
}
