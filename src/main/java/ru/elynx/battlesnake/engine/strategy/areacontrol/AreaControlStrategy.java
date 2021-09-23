package ru.elynx.battlesnake.engine.strategy.areacontrol;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;

public class AreaControlStrategy extends WeightedSearchStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ff05e6", "orca", "swoop", "1");
    }

    @Configuration
    public static class AreaControlStrategyConfiguration {
        @Bean("TasteOfSpace")
        public Supplier<IGameStrategy> areaControl() {
            return AreaControlStrategy::new;
        }
    }
}
