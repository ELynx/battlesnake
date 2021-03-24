package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.javatuples.KeyValue;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.strategies.shared.SnakeMovePredictor;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchV2Strategy implements IGameStrategy {
    private static final double MIN_FOOD_WEIGHT = 0.0d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double HUNGER_HEALTH_THRESHOLD = 100.0d;
    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double INEDIBLE_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double DETERRENT_WEIGHT = -Double.MAX_VALUE;
    private static final double WALL_WEIGHT = 0.0d;

    protected DoubleMatrix weightMatrix;
    protected SnakeMovePredictor snakeMovePredictor;
    protected String lastMove;

    protected boolean initialized = false;

    private WeightedSearchV2Strategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        if (initialized)
            return;

        weightMatrix = DoubleMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(), WALL_WEIGHT);

        snakeMovePredictor = new SnakeMovePredictor();

        lastMove = UP;

        initialized = true;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();

        applyHunger(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    protected void applyHunger(GameStateDto gameState) {
        double foodWeight = Util.scale(MIN_FOOD_WEIGHT, HUNGER_HEALTH_THRESHOLD - gameState.getYou().getHealth(),
                HUNGER_HEALTH_THRESHOLD, MAX_FOOD_WEIGHT);

        if (foodWeight <= 0.0)
            return;

        for (CoordsDto food : gameState.getBoard().getFood()) {
            final int x = food.getX();
            final int y = food.getY();

            weightMatrix.splash2ndOrder(x, y, foodWeight);
        }
    }

    protected void applySnakes(GameStateDto gameState) {
        snakeMovePredictor.setGameState(gameState);

        final CoordsDto ownHead = gameState.getYou().getHead();
        final int ownSize = gameState.getYou().getLength();
        final String ownId = gameState.getYou().getId();

        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();

            // manage head
            final String id = snake.getId();
            if (!id.equals(ownId)) {
                double baseWeight;

                final int size = body.size();
                if (size < ownSize) {
                    baseWeight = snake.getLatency() == 0
                            ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT
                            : LESSER_SNAKE_HEAD_WEIGHT;
                } else {
                    baseWeight = INEDIBLE_SNAKE_HEAD_WEIGHT;
                }

                if (baseWeight != 0.0) {
                    final CoordsDto head = snake.getHead();
                    final int x = head.getX();
                    final int y = head.getY();

                    if (Util.manhattanDistance(head, ownHead) > 4) {
                        // cheap and easy on faraway snakes
                        weightMatrix.splash1stOrder(x, y, baseWeight);
                    } else {
                        // spread hunt/danger weights
                        List<KeyValue<CoordsDto, Double>> predictions = snakeMovePredictor.predict(snake);
                        predictions.forEach(prediction -> {
                            final int px = prediction.getKey().getX();
                            final int py = prediction.getKey().getY();
                            final double pw = baseWeight * prediction.getValue();

                            weightMatrix.splash2ndOrder(px, py, pw, 4.0);
                        });
                    }
                }
            }

            // always last - mark body as impassable
            for (CoordsDto coordsDto : body) {
                final int x = coordsDto.getX();
                final int y = coordsDto.getY();

                weightMatrix.setValue(x, y, SNAKE_BODY_WEIGHT);
            }
        }
    }

    protected void applyHazards(GameStateDto gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, DETERRENT_WEIGHT);
        }
    }

    private double getCrossWeight(int x, int y) {
        double result = weightMatrix.getValue(x, y - 1);
        result += weightMatrix.getValue(x - 1, y);
        result += weightMatrix.getValue(x, y);
        result += weightMatrix.getValue(x + 1, y);
        result += weightMatrix.getValue(x, y + 1);
        return result;
    }

    private List<Triplet<String, Integer, Integer>> rank(List<Triplet<String, Integer, Integer>> toRank) {
        // filter all that go outside of map
        // sort by weight of immediate action
        // if equal options remain, sort by weight of following actions
        // drop moves that would doom the snake
        // this _does not_ guarantee that following moves are safe, only this one
        // this is just a cut on number of filtered elements
        return toRank.stream().filter(triplet -> !weightMatrix.isOutOfBounds(triplet.getValue1(), triplet.getValue2()))
                .sorted(Comparator
                        .comparingDouble((Triplet<String, Integer, Integer> triplet) -> weightMatrix
                                .getValue(triplet.getValue1(), triplet.getValue2()))
                        .thenComparingDouble((Triplet<String, Integer, Integer> triplet) -> getCrossWeight(
                                triplet.getValue1(), triplet.getValue2()))
                        .reversed())
                .dropWhile(triplet -> false).collect(Collectors.toList());
    }

    protected String bestMove(CoordsDto head) {
        final int x = head.getX();
        final int y = head.getY();

        List<Triplet<String, Integer, Integer>> ranked = new LinkedList<>();
        ranked.add(new Triplet<>(DOWN, x, y - 1));
        ranked.add(new Triplet<>(LEFT, x - 1, y));
        ranked.add(new Triplet<>(RIGHT, x + 1, y));
        ranked.add(new Triplet<>(UP, x, y + 1));

        ranked = rank(ranked);

        if (ranked.isEmpty())
            return lastMove;

        return ranked.get(0).getValue0();
    }

    protected String makeMove(GameStateDto gameState) {
        applyGameState(gameState);
        return bestMove(gameState.getYou().getHead());
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ef9600", "smile", "sharp", "1a X");
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        initOnce(gameState);
        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        // for test compatibility
        initOnce(gameState);
        final String move = makeMove(gameState);
        lastMove = move;
        return new Move(move, "New and theoretically improved");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class WeightedSearchV2StrategyConfiguration {
        @Bean("Snake_1a")
        public Supplier<IGameStrategy> nextGenWeightedSearch() {
            return WeightedSearchV2Strategy::new;
        }
    }
}
