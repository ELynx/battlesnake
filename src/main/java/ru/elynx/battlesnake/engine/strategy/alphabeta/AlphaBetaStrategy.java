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

public class AlphaBetaStrategy implements IGameStrategy {
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
    public Optional<MoveCommand> processMove(GameState state0) {
        Stream<MoveCommand> moves = sensibleYouMoves(state0);
        Stream<Pair<MoveCommand, Integer>> scoredMoves = moves.map(x -> new Pair<>(x, forMoveCommand(state0, x, 1)));
        Stream<Triplet<MoveCommand, Integer, Integer>> nudgedMoves = scoredMoves
                .map(x -> new Triplet<>(x.getValue0(), x.getValue1(), nudge(x.getValue0(), state0)));
        return nudgedMoves.max(Comparator.<Triplet<MoveCommand, Integer, Integer>>comparingInt(Triplet::getValue1)
                .thenComparingInt(Triplet::getValue2)).map(Triplet::getValue0);
    }

    private Stream<MoveCommand> sensibleYouMoves(GameState gameState) {
        return gameState.getYou().getAdvancingMoves().stream()
                .filter(x -> !gameState.getBoard().getDimensions().isOutOfBounds(x))
                .map(CoordinatesWithDirection::getDirection);
    }

    private int forMoveCommand(GameState step0, MoveCommand moveCommand, int depth) {
        var stepFunction = makeStepFunction(moveCommand);
        GameState step1 = GameStateAdvancer.advance(step0, stepFunction);

        var step1Score = GameStateScoreMaker.makeYouScore(step0, step1);

        if (Boolean.TRUE.equals(step1Score.getValue0())) {
            return (MAX_DEPTH_FOR_ADVANCE - depth + 1) * step1Score.getValue1();
        }

        if (depth >= MAX_DEPTH_FOR_ADVANCE) {
            return step1Score.getValue1();
        }

        int step2ScoreMax = sensibleYouMoves(step1).mapToInt(x -> forMoveCommand(step1, x, depth + 1)).max()
                .orElse(Integer.MIN_VALUE);

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

    private int nudge(MoveCommand moveCommand, GameState gameState) {
        Coordinates center = gameState.getBoard().getDimensions().center();
        Coordinates head = gameState.getYou().getHead();

        if (head.getY() < center.getY()) {
            if (head.getX() < center.getX()) {
                return moveCommand == UP ? 1 : 0;
            } else {
                return moveCommand == LEFT ? 1 : 0;
            }
        } else {
            if (head.getX() < center.getX()) {
                return moveCommand == RIGHT ? 1 : 0;
            } else {
                return moveCommand == DOWN ? 1 : 0;
            }
        }
    }

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta0() {
            return () -> new AlphaBetaStrategy(new WeightedSearchStrategy());
        }
    }
}
