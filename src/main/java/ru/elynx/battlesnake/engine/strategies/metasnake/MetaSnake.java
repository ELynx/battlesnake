package ru.elynx.battlesnake.engine.strategies.metasnake;

import static ru.elynx.battlesnake.protocol.Move.Moves.UP;

import java.util.List;
import java.util.function.Supplier;
import org.javatuples.Quartet;
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

    protected String lastMove = UP;

    protected MetaSnake(IMetaEnabledGameStrategy engine) {
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
        engine.processStart(gameState);
        engine.exitToRealspace(gameState);

        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        engine.resetMetaspace();
        List<Quartet<String, Integer, Integer, Double>> moves = engine.processMoveMeta(gameState);
        engine.exitToRealspace(gameState);

        if (!moves.isEmpty()) {
            lastMove = moves.get(0).getValue0();
        }

        return new Move(lastMove);
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
                Supplier<WeightedSearchV2Strategy> engineSupplier = WeightedSearchV2Strategy.WeightedSearchV2StrategyConfiguration
                        .weightedSearchMeta();
                return new MetaSnake(engineSupplier.get());
            };
        }
    }
}
