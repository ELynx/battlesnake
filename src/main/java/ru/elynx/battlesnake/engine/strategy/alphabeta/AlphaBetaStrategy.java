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
    private static final int MAX_DEPTH_FOR_ADVANCE = 4;

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
        Stream<MoveCommand> moves = sensibleYouMoves(state0);
        Stream<Pair<MoveCommand, Integer>> scoredMoves = moves.map(x -> new Pair<>(x, forMoveCommand(state0, x, 1)));
        return scoredMoves.max(Comparator.comparingInt(Pair::getValue1)).map(Pair::getValue0);
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
            return step1Score.getValue1();
        }

        if (depth > MAX_DEPTH_FOR_ADVANCE) {
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

    @Configuration
    public static class AlphaBetaStrategyConfiguration {
        @Bean("Voxel")
        public Supplier<IGameStrategy> alphaBeta() {
            return AlphaBetaStrategy::new;
        }
    }
}
