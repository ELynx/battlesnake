package ru.elynx.battlesnake.engine.strategies;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

import java.util.function.Supplier;

public class ChessStrategy implements IGameStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#268bd2", "beluga", "block-bum", "noob");
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        return new Move("UP", "e4e2");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class ChessStrategyConfiguration {
        @Bean("ChesssMassster")
        public Supplier<IGameStrategy> chess() {
            return () -> new ChessStrategy();
        }
    }
}
