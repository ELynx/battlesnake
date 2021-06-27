package ru.elynx.battlesnake.engine.strategy.solver;

import static ru.elynx.battlesnake.entity.MoveCommand.REPEAT_LAST;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class SolverStrategy implements IGameStrategy {
    private MoveCommandField whereToGo;
    private int stage = Integer.MIN_VALUE;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#268bd2", "beluga", "block-bum", "1");
    }

    @Override
    public Void processStart(GameState gameState) {
        return null;
    }

    @Override
    public Move processMove(GameState gameState) {
        return new Move(makeMove(gameState));
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

    /**
     * Derive the stage of solution from game state. Stage is then used to create
     * movement commands field.
     *
     * @param gameState
     *            to be analyzed
     * @return Derived stage
     */
    protected int calculateStage(GameState gameState) {
        return stage;
    }

    /**
     * Based on the stage, create movement command field. Cached by caller on stage
     * change.
     *
     * @param stage
     *            to create commands for
     * @param dimensions
     *            of required movement commands field
     * @return Movement commands field
     */
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
    public Void processEnd(GameState gameState) {
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
