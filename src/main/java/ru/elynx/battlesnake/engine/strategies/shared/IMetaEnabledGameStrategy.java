package ru.elynx.battlesnake.engine.strategies.shared;

import java.util.List;
import org.javatuples.Quartet;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.GameStateDto;

public interface IMetaEnabledGameStrategy extends IGameStrategy {
    /**
     * set state at move previous to current, if jumped across timelines
     */
    void setLastMove(GameStateDto gameStateDto);

    /**
     * Provide not only first choice move, but detailed explanation
     *
     * @param gameStateDto
     * @return List of Move, X, Y, Weight
     */
    List<Quartet<String, Integer, Integer, Double>> processMoveMeta(GameStateDto gameStateDto);
}
