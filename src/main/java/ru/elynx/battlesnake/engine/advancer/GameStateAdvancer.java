package ru.elynx.battlesnake.engine.advancer;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import ru.elynx.battlesnake.entity.Board;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;

public class GameStateAdvancer {
    private GameStateAdvancer() {
    }

    public static GameState advance(GameState current, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        int turn = makeTurn(current);
        List<Snake> snakes = makeSnakes(current, moveDecider);

        return assemble(current, turn, snakes);
    }

    private static GameState assemble(GameState current, int turn, List<Snake> snakes) {
        Snake you = snakes.stream().filter(someSnake -> someSnake.getId().equals(current.getYou().getId())).findFirst()
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
        return gameState.getBoard().getSnakes();
    }
}
