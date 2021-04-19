package ru.elynx.battlesnake.engine.strategies.thoughtful;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.strategies.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public class ThoughtfulSnake implements IGameStrategy {
    IGameStrategy plug;

    protected ThoughtfulSnake() {
        plug = new WeightedSearchStrategy.WeightedSearchStrategyConfiguration().weightedSearch().get();
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ef9600", "shades", "rbc-necktie", "1");
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        return plug.processStart(gameState);
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        return plug.processMove(gameState);
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return plug.processEnd(gameState);
    }

    @Configuration
    public static class ThoughtfulSnakeConfiguration {
        @Bean("The-serpent-saves-us-from-thought")
        public Supplier<IGameStrategy> thoughtful() {
            return ThoughtfulSnake::new;
        }
    }
}
