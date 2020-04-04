package ru.elynx.battlesnake.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameState;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    private Logger logger = LoggerFactory.getLogger(GameStrategyFactory.class);

    public IGameStrategy makeGameStrategy(GameState gameState) {
        String snakeName = "undefined";

        try {
            snakeName = gameState.getYou().getName();

            if ("Snake 1".equals(snakeName)) {
                return getGameStrategy(0);
            }

            if ("Snake 1a".equals(snakeName)) {
                return getGameStrategy(1);
            }
        } catch (Exception e) {
            logger.error("Exception choosing game strategy", e);
        }

        logger.warn("Unknown or problematic snake name [" + snakeName + "]");
        return getGameStrategy(0);
    }

    @Override
    public int getGameStrategySize() {
        return 2;
    }

    @Override
    public IGameStrategy getGameStrategy(int index) throws IllegalArgumentException {
        if (index < 0 || index > getGameStrategySize()) {
            throw new IllegalArgumentException("Illegal strategy index " + index);
        }

        if (0 == index) {
            return WeightedSearchStrategy.wallWeightNegativeOne();
        }

        if (1 == index) {
            return WeightedSearchStrategy.wallWeightZero();
        }

        throw new IllegalArgumentException("Unknown strategy index " + index);
    }
}
