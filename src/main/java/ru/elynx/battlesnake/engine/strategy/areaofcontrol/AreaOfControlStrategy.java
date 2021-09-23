package ru.elynx.battlesnake.engine.strategy.areaofcontrol;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;

public class AreaOfControlStrategy extends WeightedSearchStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ff05e6", "orca", "swoop", "1");
    }

    @Configuration
    public static class AreaOfControlStrategyConfiguration {
        @Bean("Taste of Space")
        public Supplier<IGameStrategy> areaOfControl() {
            return AreaOfControlStrategy::new;
        }
    }
}
