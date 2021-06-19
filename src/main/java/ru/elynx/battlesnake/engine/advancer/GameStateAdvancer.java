package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import ru.elynx.battlesnake.entity.*;

public class GameStateAdvancer {
    private GameStateAdvancer() {
    }

    public static GameState advance(GameState current, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        int turn = makeTurn(current);
        List<Snake> snakes = makeSnakes(current, moveDecider);

        return assemble(current, turn, snakes);
    }

    private static GameState assemble(GameState current, int turn, List<Snake> snakes) {
        Snake you = snakes.stream().filter(someSnake -> someSnake.getId().equals(current.getYou().getId())).findAny()
                .orElse(null);

        if (you == null) {
            throw new IllegalStateException("You was not found");
        }

        Board board = new Board(current.getBoard().getDimensions(), current.getBoard().getFood(),
                current.getBoard().getHazards(), snakes);

        return new GameState(current.getGameId(), turn, current.getRules(), board, you);
    }

    private static int makeTurn(GameState gameState) {
        return gameState.getTurn() + 1;
    }

    private static List<Snake> makeSnakes(GameState gameState, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        List<Snake> snakes = new ArrayList<>(gameState.getBoard().getSnakes().size());

        for (Snake snake : gameState.getBoard().getSnakes()) {
            MoveCommand moveCommand = moveDecider.apply(snake, gameState);
            snakes.add(moveSnake(snake, moveCommand));
        }

        return snakes;
    }

    private static Snake moveSnake(Snake current, MoveCommand moveCommand) {
        Coordinates nextHead = current.getHead().sideNeighbours().stream()
                .filter((CoordinatesWithDirection coordinates) -> moveCommand.equals(coordinates.getDirection()))
                .findAny().orElse(null);

        if (nextHead == null) {
            throw new IllegalStateException("Could not find next head position for [" + current.getId()
                    + "] and move command [" + moveCommand + ']');
        }

        List<Coordinates> body = new ArrayList<>(current.getBody().size());
        body.add(nextHead);
        for (int i = 0; i < current.getBody().size() - 1; ++i) {
            body.add(current.getBody().get(i));
        }

        return new Snake(current.getId(), current.getName(), current.getHealth(), body, current.getLatency(), nextHead,
                body.size(), current.getShout(), current.getSquad());
    }
}
