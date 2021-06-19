package ru.elynx.battlesnake.engine.strategy.alphabeta;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Move;

public class AlphaBetaStrategy extends OmegaStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "1");
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        return super.processMove(hazardPredictor);
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
