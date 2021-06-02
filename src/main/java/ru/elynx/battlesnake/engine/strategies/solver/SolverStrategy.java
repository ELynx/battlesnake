package ru.elynx.battlesnake.engine.strategies.solver;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.api.*;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.*;

public class SolverStrategy implements IGameStrategy {
    private MoveCommandField whereToGo;
    private int stage = Integer.MIN_VALUE;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#268bd2", "beluga", "block-bum", "noob");
    }

    protected int calculateStage(GameState gameState) {
        return stage;
    }

    protected MoveCommandField makeDirections(int stage, Dimensions dimensions) {
        return null;
    }

    protected MoveCommand makeMove(GameState gameState) {
        int newStage = calculateStage(gameState);
        if (newStage != stage) {
            Board board = gameState.getBoard();
            whereToGo = makeDirections(newStage, board.getDimensions());
            stage = newStage;
        }

        if (whereToGo != null) {
            Coordinates head = gameState.getYou().getHead();
            return whereToGo.getMoveCommand(head);
        } else {
            return REPEAT_LAST;
        }
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        return new Move(makeMove(hazardPredictor.getGameState()), "e4e2");
    }

    @Override
    public Void processEnd(HazardPredictor hazardPredictor) {
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
