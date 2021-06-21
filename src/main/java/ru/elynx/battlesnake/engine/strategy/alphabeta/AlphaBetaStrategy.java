package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy extends OmegaStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "1");
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        GameState state0 = hazardPredictor.getGameState();

        Stream<MoveCommand> moves = Stream.of(DOWN, LEFT, RIGHT, UP);

        Optional<MoveCommand> moveCommand = moves
                .max(Comparator.comparingInt((MoveCommand move) -> forMoveCommand(state0, move)));
        return new Move(moveCommand.orElse(REPEAT_LAST));
    }

    private int forMoveCommand(GameState step0, MoveCommand moveCommand) {
        BiFunction<Snake, GameState, MoveCommand> step1MoveFunction = (Snake snake, GameState gameState) -> {
            if (gameState.getYou().getId().equals(snake.getId())) {
                return moveCommand;
            }

            return bestMoveForSnake(snake, gameState).orElse(UP);
        };

        GameState step1 = GameStateAdvancer.advance(step0, step1MoveFunction);
        if (step1.isYouEliminated()) {
            return -100;
        }

        // score for arriving here
        ScoreMaker scoreMaker0 = makeScoreMaker(step0.getYou(), step0);
        int score0 = scoreMaker0.scoreMove(step0.getYou().getHead().move(moveCommand));

        // score for all possible moves
        ScoreMaker scoreMaker1 = makeScoreMaker(step1.getYou(), step1);

        int score1 = 0;
        for (CoordinatesWithDirection coordinates : step1.getYou().getHead().sideNeighbours()) {
            score1 += scoreMaker1.scoreMove(coordinates);
        }

        // TODO these are random weights to pass the tests
        return 4 * score0 + score1;
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
