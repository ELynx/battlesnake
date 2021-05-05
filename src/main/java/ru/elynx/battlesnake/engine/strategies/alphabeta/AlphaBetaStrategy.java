package ru.elynx.battlesnake.engine.strategies.alphabeta;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;

public class AlphaBetaStrategy extends OmegaStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "1");
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> thoughtful() {
            return AlphaBetaStrategy::new;
        }
    }
}
