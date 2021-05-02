package ru.elynx.battlesnake.engine.strategies.thoughtful;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.Move;

public class ThoughtfulSnake implements IGameStrategy {
    protected ThoughtfulSnake() {
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "shades", "rbc-necktie", "1");
    }

    @Override
    public Void processStart(GameStatePredictor gameState) {
        throw new java.lang.UnsupportedOperationException("TODO");
    }

    @Override
    public Move processMove(GameStatePredictor gameState) {
        throw new java.lang.UnsupportedOperationException("TODO");
    }

    @Override
    public Void processEnd(GameStatePredictor gameState) {
        throw new java.lang.UnsupportedOperationException("TODO");
    }

    @Override
    public boolean isCombatant() {
        return true;
    }

    @Configuration
    public static class ThoughtfulSnakeConfiguration {
        @Bean("The-serpent-saves-us-from-thought")
        public Supplier<IGameStrategy> thoughtful() {
            return ThoughtfulSnake::new;
        }
    }
}
