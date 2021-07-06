package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class AlphaBetaStrategy implements IGameStrategy {
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
        Stream<MoveCommand> moves = Arrays.stream(values());
        return moves.max(Comparator.comparingInt((MoveCommand move) -> forMoveCommand(state0, move)));
    }

    private int forMoveCommand(GameState step0, MoveCommand moveCommand) {
        var stepFunction = makeStepFunction(moveCommand);
        GameState step1 = GameStateAdvancer.advance(step0, stepFunction);

        if (isEliminated(step1)) {
            return -100;
        }

        // if one snake left and not eliminated then victory
        if (oneSnakeLeft(step0, step1)) {
            return 100;
        }

        // TODO score state, preferably with predictions
        return 0;
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
