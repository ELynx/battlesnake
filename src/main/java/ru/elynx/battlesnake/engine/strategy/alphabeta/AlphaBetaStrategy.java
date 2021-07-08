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
    private static int MAX_DEPTH_FOR_ADVANCE = 2;

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

        int score1 = 1;

        // simulation termination cases <<

        // loss
        if (isEliminated(step1)) {
            score1 = -100; // TODO score from method
            return score1;
        }

        // victory
        if (oneSnakeLeft(step0, step1)) {
            score1 = 100; // TODO score from method
            return score1;
        }

        // depth exceeded
        if (depth > MAX_DEPTH_FOR_ADVANCE) {
            return score1;
        }

        // >>

        // will always be initiated, at least 3 iterations are guaranteed in loop
        int minScore = Integer.MAX_VALUE;
        int maxScore = Integer.MIN_VALUE;

        for (CoordinatesWithDirection coordinates : step1.getYou().getAdvancingMoves()) {
            int score2i = forMoveCommand(step1, coordinates.getDirection(), depth);

            if (score2i < minScore) {
                minScore = score2i;
            }

            if (score2i > maxScore) {
                maxScore = score2i;
            }
        }

        return score1 + 3 * maxScore / 4 + minScore / 4;
    }

    private BiFunction<Snake, GameState, MoveCommand> makeStepFunction(MoveCommand moveCommand) {
        return (Snake snake, GameState gameState) -> {
            if (gameState.getYou().getId().equals(snake.getId())) {
                return moveCommand;
            }

            return polySnakeGameStrategy.processMove(snake, gameState).orElse(UP);
        };
    }

    /**
     * Test if `you` lost between two game states
     *
     * @param gameState1
     *            tested to see if `you` lost
     * @return true if `you` lost
     */
    private boolean isEliminated(GameState gameState1) {
        return gameState1.isYouEliminated();
    }

    /**
     * Test if `you` became last snake between turns
     *
     * @param gameState0
     *            to check how many snakes there were before
     * @param gameState1
     *            to check how many snakes left
     * @return true if there is one snake left
     */
    private boolean oneSnakeLeft(GameState gameState0, GameState gameState1) {
        return gameState0.getBoard().getSnakes().size() > 1 && gameState1.getBoard().getSnakes().size() == 1;
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
