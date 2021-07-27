package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Data;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.engine.strategy.weightedsearch.WeightedSearchStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy implements IGameStrategy {
    private final IPolySnakeGameStrategy polySnakeGameStrategy;
    private final int maxSearchDepth;

    public AlphaBetaStrategy(IPolySnakeGameStrategy polySnakeGameStrategy, int maxSearchDepth) {
        this.polySnakeGameStrategy = polySnakeGameStrategy;
        this.maxSearchDepth = maxSearchDepth;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "chomp", "freckled", "2");
    }

    @Override
    public void init(GameState gameState) {
        init(gameState.getYou(), gameState);
    }

    private void init(Snake snake, GameState gameState) {
        polySnakeGameStrategy.init(gameState);
        polySnakeGameStrategy.setPrimarySnake(snake);
    }

    @Override
    public Optional<MoveCommand> processMove(GameState gameState) {
        return processMove(gameState.getYou(), gameState);
    }

    private Optional<MoveCommand> processMove(Snake snake, GameState gameState) {
        return bestMove(snake, gameState);
    }

    private Optional<MoveCommand> bestMove(Snake snake, GameState gameState) {
        return getScoredMoves(snake, gameState)
                .max(Comparator.<Triplet<MoveCommand, Long, Long>>comparingLong(Triplet::getValue1)
                        .thenComparingLong(Triplet::getValue2))
                .map(Triplet::getValue0);
    }

    private Stream<Triplet<MoveCommand, Long, Long>> getScoredMoves(Snake snake, GameState gameState) {
        Optional<MoveCommand> tieResolveMove = polySnakeGameStrategy.processMove(snake, gameState);
        return getAlphaBetaMoves(snake, gameState).map(x -> new Triplet<>(x.getValue0(), x.getValue1(),
                tieResolveMove.map(y -> y.equals(x.getValue0()) ? 1L : 0L).orElse(0L)));
    }

    private Stream<Pair<MoveCommand, Long>> getAlphaBetaMoves(Snake snake, GameState gameState) {
        return sensibleMoves(snake, gameState).map(moveCommand -> new Pair<>(moveCommand,
                forMoveCommand(GameStateIteration.rootIteration(moveCommand, snake, gameState))));
    }

    private Stream<MoveCommand> sensibleMoves(Snake snake, GameState gameState) {
        Dimensions dimensions = gameState.getBoard().getDimensions();
        return snake.getAdvancingMoves().stream().filter(x -> !dimensions.isOutOfBounds(x))
                .map(CoordinatesWithDirection::getDirection);
    }

    @Data
    private static class GameStateIteration {
        private final int depth;
        private final MoveCommand moveCommand;
        private final Snake snake;
        private final GameState gameState;

        public static GameStateIteration rootIteration(MoveCommand moveCommand, Snake snake, GameState gameState) {
            return new GameStateIteration(1, moveCommand, snake, gameState);
        }

        public GameStateIteration nextIteration(MoveCommand moveCommand, Snake snake, GameState gameState) {
            return new GameStateIteration(depth + 1, moveCommand, snake, gameState);
        }
    }

    private long forMoveCommand(GameStateIteration step0) {
        var stepFunction0 = makeStepFunction(step0.getMoveCommand(), step0.getSnake());
        var steps1 = GameStateAdvancer.advance(stepFunction0, step0.getSnake(), step0.getGameState());
        return steps1.mapToLong(x -> calculatePossibleStateScore(step0, x)).sum();
    }

    private long calculatePossibleStateScore(GameStateIteration step0, Pair<GameState, Double> possibleStep1) {
        GameState step1 = possibleStep1.getValue0();
        long stateScore = calculateStateScore(step0, step1);
        return Math.round(possibleStep1.getValue1() * stateScore);
    }

    private long calculateStateScore(GameStateIteration step0, GameState step1) {
        var step1Score = GameStateScoreMaker.makeScore(step0.getSnake(), step0.getGameState(), step1);

        if (Boolean.TRUE.equals(step1Score.getValue0())) {
            return (maxSearchDepth - step0.getDepth() + 1) * step1Score.getValue1();
        }

        if (step0.getDepth() >= maxSearchDepth) {
            return step1Score.getValue1();
        }

        Optional<Snake> snake1Optional = step1.getBoard().getSnakes().stream()
                .filter(x -> x.getId().equals(step0.getSnake().getId())).findAny();

        if (snake1Optional.isEmpty()) {
            return step1Score.getValue1() + Long.MIN_VALUE;
        }

        Snake snake1 = snake1Optional.get();

        long step2ScoreMax = sensibleMoves(snake1, step1)
                .mapToLong(moveCommand1 -> forMoveCommand(step0.nextIteration(moveCommand1, snake1, step1))).max()
                .orElse(Long.MIN_VALUE);

        return step1Score.getValue1() + step2ScoreMax;
    }

    private BiFunction<Snake, GameState, List<MoveCommandWithProbability>> makeStepFunction(MoveCommand moveCommand,
            Snake snake) {
        return (Snake someSnake, GameState gameState) -> {
            if (someSnake.getId().equals(snake.getId())) {
                return MoveCommandWithProbability.onlyFrom(moveCommand);
            }

            var fromPoly = polySnakeGameStrategy.processMoveWithProbabilities(someSnake, gameState);
            if (fromPoly.isEmpty()) {
                return MoveCommandWithProbability.onlyFrom(UP);
            } else {
                return fromPoly;
            }
        };
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta0() {
            return () -> new AlphaBetaStrategy(new WeightedSearchStrategy(), 5);
        }
    }
}
