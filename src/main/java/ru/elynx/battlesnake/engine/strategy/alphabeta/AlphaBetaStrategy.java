package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy extends OmegaStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "1");
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        GameState gameState = hazardPredictor.getGameState();

        MoveCommand moveCommand = DOWN;
        int bestScore = forMoveCommand(gameState, moveCommand);

        int tmp = forMoveCommand(gameState, LEFT);
        if (tmp > bestScore) {
            moveCommand = LEFT;
            bestScore = tmp;
        }

        tmp = forMoveCommand(gameState, RIGHT);
        if (tmp > bestScore) {
            moveCommand = RIGHT;
            bestScore = tmp;
        }

        tmp = forMoveCommand(gameState, UP);
        if (tmp > bestScore) {
            moveCommand = UP;
        }

        return new Move(moveCommand);
    }

    private int forMoveCommand(GameState step0, MoveCommand moveCommand) {
        CoordinatesWithDirection headAtStep1 = step0.getYou().getHead().move(moveCommand);

        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(step0, coordinates -> occupiedPositions.set(coordinates));

        if (!isWalkable(headAtStep1)) {
            return -100;
        }

        BiFunction<Snake, GameState, MoveCommand> step1Function = (Snake snake, GameState gameState) -> {
            if (gameState.getYou().getId().equals(snake.getId())) {
                return moveCommand;
            }

            return bestMoveForSnake(snake, gameState).orElse(UP);
        };

        GameState step1 = GameStateAdvancer.advance(step0, step1Function);

        boolean found = false;
        for (Snake someSnake : step1.getBoard().getSnakes()) {
            if (someSnake.getId().equals(step1.getYou().getId())) {
                found = true;
                break;
            }
        }

        // eliminated
        if (!found) {
            return -100;
        }

        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(step1, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker = new ScoreMaker(step1.getYou(), step1);
        int score = scoreMaker.scoreHead();
        for (CoordinatesWithDirection coordinates : step1.getYou().getHead().sideNeighbours()) {
            if (isWalkable(coordinates)) {
                score += scoreMaker.scoreMove(coordinates);
            }
        }

        return score;
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
