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
        BiFunction<Snake, GameState, MoveCommand> step1MoveFunction = (Snake snake, GameState gameState) -> {
            if (gameState.getYou().getId().equals(snake.getId())) {
                return moveCommand;
            }

            return bestMoveForSnake(snake, gameState).orElse(UP);
        };

        GameState step1 = GameStateAdvancer.advance(step0, step1MoveFunction);

        boolean eliminated = true;
        for (Snake someSnake : step1.getBoard().getSnakes()) {
            if (someSnake.getId().equals(step1.getYou().getId())) {
                eliminated = false;
                break;
            }
        }

        if (eliminated) {
            return -100;
        }

        // score for arriving here
        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(step0, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker0 = new ScoreMaker(step0.getYou(), step0, this);
        int score = scoreMaker0.scoreMove(step0.getYou().getHead().move(moveCommand));

        // score for all possible moves
        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(step1, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker = new ScoreMaker(step1.getYou(), step1, this);

        for (CoordinatesWithDirection coordinates : step1.getYou().getHead().sideNeighbours()) {
            if (!coordinates.equals(step0.getYou().getHead())) {
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
