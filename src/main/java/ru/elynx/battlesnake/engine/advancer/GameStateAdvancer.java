package ru.elynx.battlesnake.engine.advancer;

import java.util.function.BiFunction;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;

public class GameStateAdvancer {
    private GameStateAdvancer() {
    }

    public static GameState advance(GameState current, BiFunction<Snake, GameState, MoveCommand> moveDecider) {
        return current;
    }
}
