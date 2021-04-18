package ru.elynx.battlesnake.engine.strategies.legacy;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public class LegacyGameStrategy implements IGameStrategy {
    private final BattlesnakeInfo battlesnakeInfo;

    LegacyGameStrategy(BattlesnakeInfo battlesnakeInfo) {
        this.battlesnakeInfo = battlesnakeInfo;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return battlesnakeInfo;
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        throw new UnsupportedOperationException("Legacy strategy does not support start");
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        throw new UnsupportedOperationException("Legacy strategy does not support move");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        throw new UnsupportedOperationException("Legacy strategy does not support end");
    }

    @Override
    public boolean isCombatant() {
        return false;
    }

    @Configuration
    public static class LegacyStrategyConfiguration {
        @Bean("Snake_1")
        public Supplier<IGameStrategy> archiveWeightedSearch() {
            return () -> new LegacyGameStrategy(new BattlesnakeInfo("ELynx", "#cecece", "smile", "sharp", "archival"));
        }
    }
}
