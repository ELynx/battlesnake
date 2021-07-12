package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.engine.strategy.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy implements IPolySnakeGameStrategy {
    private static final int MAX_DEPTH_FOR_ADVANCE = 5;

    IPolySnakeGameStrategy polySnakeGameStrategy;

    public AlphaBetaStrategy(IPolySnakeGameStrategy polySnakeGameStrategy) {
        this.polySnakeGameStrategy = polySnakeGameStrategy;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "2");
    }

    @Override
    public void init(GameState gameState) {
        polySnakeGameStrategy.init(gameState);
    }

    @Override
    public Optional<MoveCommand> processMove(Snake snake, GameState gameState) {
        Stream<MoveCommand> moves = sensibleMoves(snake, gameState);

        Stream<Pair<MoveCommand, Integer>> alphaBetaMoves = moves
                .map(x -> new Pair<>(x, forMoveCommand(1, x, snake, gameState)));

        Optional<MoveCommand> tieResolveMove = polySnakeGameStrategy.processMove(snake, gameState);
        Stream<Triplet<MoveCommand, Integer, Integer>> scoredMoves = alphaBetaMoves
                .map(x -> new Triplet<>(x.getValue0(), x.getValue1(),
                        tieResolveMove.map(y -> y.equals(x.getValue0()) ? 1 : 0).orElse(0)));

        return scoredMoves.max(Comparator.<Triplet<MoveCommand, Integer, Integer>>comparingInt(Triplet::getValue1)
                .thenComparingInt(Triplet::getValue2)).map(Triplet::getValue0);
    }

    private Stream<MoveCommand> sensibleMoves(Snake snake, GameState gameState) {
        return snake.getAdvancingMoves().stream().filter(x -> !gameState.getBoard().getDimensions().isOutOfBounds(x))
                .map(CoordinatesWithDirection::getDirection);
    }

    private int forMoveCommand(int depth, MoveCommand moveCommand, Snake snake, GameState step0) {
        var stepFunction = makeStepFunction(moveCommand, snake);
        GameState step1 = GameStateAdvancer.advance(step0, stepFunction, snake);

        var step1Score = GameStateScoreMaker.makeScore(snake, step0, step1);

        if (Boolean.TRUE.equals(step1Score.getValue0())) {
            return (MAX_DEPTH_FOR_ADVANCE - depth + 1) * step1Score.getValue1();
        }

        if (depth >= MAX_DEPTH_FOR_ADVANCE) {
            return step1Score.getValue1();
        }

        int step2ScoreMax = sensibleMoves(snake, step1).mapToInt(x -> forMoveCommand(depth + 1, x, snake, step1)).max()
                .orElse(Integer.MIN_VALUE);

        return step1Score.getValue1() + step2ScoreMax;
    }

    private BiFunction<Snake, GameState, MoveCommand> makeStepFunction(MoveCommand moveCommand, Snake snake) {
        return (Snake someSnake, GameState gameState) -> {
            if (someSnake.getId().equals(snake.getId())) {
                return moveCommand;
            }

            return polySnakeGameStrategy.processMove(someSnake, gameState).orElse(UP);
        };
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta0() {
            return () -> new AlphaBetaStrategy(new WeightedSearchStrategy());
        }
    }
}
