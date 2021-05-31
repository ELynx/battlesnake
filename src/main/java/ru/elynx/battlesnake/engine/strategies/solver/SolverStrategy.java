package ru.elynx.battlesnake.engine.strategies.solver;

import static ru.elynx.battlesnake.entity.Move.Moves.*;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.api.*;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Move;

public class SolverStrategy implements IGameStrategy {
    private StringField whereToGo;
    private int stage = Integer.MIN_VALUE;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#268bd2", "beluga", "block-bum", "noob");
    }

    protected int calculateStage(GameStateDto gameStateDto) {
        return Integer.MIN_VALUE;
    }

    protected StringField makeDirections(int stage, int width, int height) {
        return null;
    }

    protected String makeMove(GameStateDto gameStateDto) {
        final int newStage = calculateStage(gameStateDto);
        if (newStage != stage) {
            final BoardDto board = gameStateDto.getBoard();
            whereToGo = makeDirections(newStage, board.getWidth(), board.getHeight());
            stage = newStage;
        }

        if (whereToGo != null) {
            final CoordsDto head = gameStateDto.getYou().getHead();
            return whereToGo.getString(head.getX(), head.getY());
        } else {
            return UP;
        }
    }

    @Override
    public Void processStart(GameStatePredictor gameState) {
        return null;
    }

    @Override
    public Move processMove(GameStatePredictor gameState) {
        return new Move(makeMove(gameState), "e4e2");
    }

    @Override
    public Void processEnd(GameStatePredictor gameState) {
        return null;
    }

    @Override
    public boolean isCombatant() {
        return false;
    }

    @Configuration
    public static class SolverStrategyConfiguration {
        @Bean("ChesssMassster")
        public Supplier<IGameStrategy> chess() {
            return SoloLengthChallengeStrategy::new;
        }
    }
}
