package ru.elynx.battlesnake.engine.strategies.shared;

import java.util.List;
import org.javatuples.Quartet;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public interface IMetaEnabledGameStrategy extends IGameStrategy {
    /**
     * Provide not only first choice move, but detailed explanation
     *
     * @param gameStateDto
     * @return List of Move, X, Y, Weight
     */
    List<Quartet<String, Integer, Integer, Double>> processMoveMeta(GameStateDto gameStateDto);

    /**
     * calls to processMove after this call are meta
     */
    void enterMetaspace();

    /**
     * reset inner state to receive new meta branch
     */
    void resetMetaspace();

    /**
     * meta is over, turn decision was made
     */
    void exitMetaspace();

    /**
     * @param move
     *            decided by meta
     */
    default void processDecision(Move move) {
    }
}
