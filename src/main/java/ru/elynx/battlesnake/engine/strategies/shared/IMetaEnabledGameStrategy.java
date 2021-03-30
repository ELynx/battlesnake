package ru.elynx.battlesnake.engine.strategies.shared;

import ru.elynx.battlesnake.protocol.Move;

public interface IMetaEnabledGameStrategy {
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
     * @param move Move that was decided by meta
     */
    default void processMetaMove(Move move) {}
}
