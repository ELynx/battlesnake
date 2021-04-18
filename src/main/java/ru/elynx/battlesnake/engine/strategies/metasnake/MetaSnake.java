package ru.elynx.battlesnake.engine.strategies.metasnake;

import java.util.List;
import java.util.function.Supplier;
import org.javatuples.Quartet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.strategies.shared.IMetaEnabledGameStrategy;
import ru.elynx.battlesnake.engine.strategies.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public class MetaSnake implements IGameStrategy {
    protected final IMetaEnabledGameStrategy engine;

    protected MetaSnake(IMetaEnabledGameStrategy engine) {
        this.engine = engine;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        BattlesnakeInfo battlesnakeInfo = engine.getBattesnakeInfo();
        battlesnakeInfo.setHead("shades");
        battlesnakeInfo.setTail("rbc-necktie");
        battlesnakeInfo.setVersion("meta [" + battlesnakeInfo.getVersion() + ']');

        return battlesnakeInfo;
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        engine.processStart(gameState);

        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        List<Quartet<String, Integer, Integer, Double>> moves = engine.processMoveMeta(gameState);

        if (moves.isEmpty()) {
            return new Move(); // would repeat last move
        }

        return new Move(moves.get(0).getValue0());
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return engine.processEnd(gameState);
    }

    @Configuration
    public static class MetaSnakeConfiguration {
        @Bean("The-serpent-saves-us-from-thought")
        public Supplier<IGameStrategy> metaWeightedSearch() {
            return () -> {
                Supplier<WeightedSearchStrategy> engineSupplier = WeightedSearchStrategy.WeightedSearchStrategyConfiguration
                        .weightedSearchMeta();
                return new MetaSnake(engineSupplier.get());
            };
        }
    }
}
