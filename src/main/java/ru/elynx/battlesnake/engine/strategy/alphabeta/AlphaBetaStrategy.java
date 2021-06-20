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

        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(gameState, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker = new ScoreMaker(gameState.getYou(), gameState, this);
        Coordinates head = gameState.getYou().getHead();

        MoveCommand moveCommand = DOWN;
        int bestScore = scoreMaker.scoreMove(head.move(moveCommand)) + forMoveCommand(gameState, moveCommand);

        int tmp = scoreMaker.scoreMove(head.move(LEFT)) + forMoveCommand(gameState, LEFT);
        if (tmp > bestScore) {
            moveCommand = LEFT;
            bestScore = tmp;
        }

        tmp = scoreMaker.scoreMove(head.move(RIGHT)) + forMoveCommand(gameState, RIGHT);
        if (tmp > bestScore) {
            moveCommand = RIGHT;
            bestScore = tmp;
        }

        tmp = scoreMaker.scoreMove(head.move(UP)) + forMoveCommand(gameState, UP);
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

        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(step1, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker = new ScoreMaker(step1.getYou(), step1, this);
        int score = 0;
        for (CoordinatesWithDirection coordinates : step1.getYou().getHead().sideNeighbours()) {
            score += scoreMaker.scoreMove(coordinates);
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
