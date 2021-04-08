package ru.elynx.battlesnake.engine.game;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.javatuples.Quartet;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class GameSimulator {
    protected BiFunction<GameStateDto, SnakeDto, List<Quartet<String, Integer, Integer, Double>>> makeSnakeTurn;
    protected BiFunction<GameStateDto, SnakeDto, List<Quartet<String, Integer, Integer, Double>>> makeYouTurn;

    protected Function<GameStateDto, Void> resetYouToGameState;
}
