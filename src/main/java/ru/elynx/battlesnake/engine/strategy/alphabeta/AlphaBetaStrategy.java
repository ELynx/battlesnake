package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy implements IGameStrategy {
    private static final int MAX_DEPTH_FOR_ADVANCE = 2;

    IPolySnakeGameStrategy polySnakeGameStrategy;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "2");
    }

    @Override
    public void init(GameState gameState) {
        polySnakeGameStrategy = new OmegaStrategy();
        polySnakeGameStrategy.init(gameState);
    }

    @Override
    public Optional<MoveCommand> processMove(GameState state0) {
        Stream<MoveCommand> moves = state0.getYou().getAdvancingMoves().stream()
                .map(CoordinatesWithDirection::getDirection);
        Stream<Pair<MoveCommand, Integer>> scoredMoves = moves.map(x -> new Pair<>(x, forMoveCommand(state0, x, 0)));
        return scoredMoves.max(Comparator.comparingInt(Pair::getValue1)).map(Pair::getValue0);
    }

    private int forMoveCommand(GameState step0, MoveCommand moveCommand, int depth) {
        var stepFunction = makeStepFunction(moveCommand);
        GameState step1 = GameStateAdvancer.advance(step0, stepFunction);
        ++depth;

        var step1Score = GameStateScoreMaker.makeScore(step0.getYou(), step0, step1);

        if (step1Score.getValue0()) {
            return step1Score.getValue1();
        }

        if (depth > MAX_DEPTH_FOR_ADVANCE) {
            return step1Score.getValue1();
        }

        // will always be initiated, at least 3 iterations are guaranteed in loop
        int step2ScoreMax = Integer.MIN_VALUE;

        for (CoordinatesWithDirection coordinates : step1.getYou().getAdvancingMoves()) {
            int step2Score = forMoveCommand(step1, coordinates.getDirection(), depth);

            if (step2Score > step2ScoreMax) {
                step2ScoreMax = step2Score;
            }
        }

        return step1Score.getValue1() + step2ScoreMax;
    }

    private BiFunction<Snake, GameState, MoveCommand> makeStepFunction(MoveCommand moveCommand) {
        return (Snake snake, GameState gameState) -> {
            if (gameState.getYou().getId().equals(snake.getId())) {
                return moveCommand;
            }

            return polySnakeGameStrategy.processMove(snake, gameState).orElse(UP);
        };
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
