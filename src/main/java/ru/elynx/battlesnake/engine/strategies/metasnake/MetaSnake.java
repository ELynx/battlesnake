package ru.elynx.battlesnake.engine.strategies.metasnake;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.strategies.shared.IMetaEnabledGameStrategy;
import ru.elynx.battlesnake.engine.strategies.weightedsearch.WeightedSearchV2Strategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public class MetaSnake implements IGameStrategy {
    protected final IMetaEnabledGameStrategy engine;

    private MetaSnake(IMetaEnabledGameStrategy engine) {
        this.engine = engine;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        BattlesnakeInfo battlesnakeInfo = engine.getBattesnakeInfo();
        battlesnakeInfo.setHead("shades");
        battlesnakeInfo.setVersion("meta [" + battlesnakeInfo.getVersion() + ']');

        return battlesnakeInfo;
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        return engine.processStart(gameState);
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        Move move = engine.processMove(gameState);

        engine.enterMetaspace();
        engine.resetMetaspace();
        engine.exitMetaspace();

        engine.processMetaMove(move);

        return move;
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return engine.processEnd(gameState);
    }

    @Configuration
    public static class MetaSnakeConfiguration {
        @Bean("Meta_Snake_1a")
        public Supplier<IGameStrategy> metaWeightedSearch() {
            return () -> {
                Supplier<WeightedSearchV2Strategy> engineSupplier = WeightedSearchV2Strategy.WeightedSearchV2StrategyConfiguration
                        .weightedSearchMeta();
                return new MetaSnake(engineSupplier.get());
            };
        }
    }
}
