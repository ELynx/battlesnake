package ru.elynx.battlesnake.engine.strategy.solver;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class SolverStrategy implements IGameStrategy {
    private MoveCommandField whereToGo;
    private int stage = Integer.MIN_VALUE;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#268bd2", "beluga", "block-bum", "specialist");
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        return new Move(makeMove(hazardPredictor.getGameState()));
    }

    private MoveCommand makeMove(GameState gameState) {
        processStage(gameState);

        return getMoveCommand(gameState);
    }

    private void processStage(GameState gameState) {
        int newStage = calculateStage(gameState);
        if (newStage != stage) {
            Board board = gameState.getBoard();
            whereToGo = makeDirections(newStage, board.getDimensions());
            stage = newStage;
        }
    }
    protected int calculateStage(GameState gameState) {
        return stage;
    }

    protected MoveCommandField makeDirections(int stage, Dimensions dimensions) {
        return null;
    }

    private MoveCommand getMoveCommand(GameState gameState) {
        if (whereToGo != null) {
            Coordinates head = gameState.getYou().getHead();
            return whereToGo.getMoveCommand(head);
        } else {
            return REPEAT_LAST;
        }
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
        @Bean("Solver_SoloLengthChallenge")
        public Supplier<IGameStrategy> chess() {
            return SoloLengthChallengeStrategy::new;
        }
    }
}
