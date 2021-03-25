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
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.strategies.shared.SnakeMovePredictor;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchV2Strategy implements IGameStrategy {
    private static final double WALL_WEIGHT = 0.0d;

    private static final double MIN_FOOD_WEIGHT = 0.0d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double HUNGER_HEALTH_THRESHOLD = 100.0d;

    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double INEDIBLE_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double BLOCK_INEDIBLE_HEAD_PROBABILITY = 0.85;

    private static final double DETERRENT_WEIGHT = -Double.MAX_VALUE;

    protected DoubleMatrix weightMatrix;
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected List<CoordsDto> lastFood;
    protected SnakeMovePredictor snakeMovePredictor;
    protected String lastMove;

    protected boolean initialized = false;

    private WeightedSearchV2Strategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        if (initialized)
            return;

        final int width = gameState.getBoard().getWidth();
        final int height = gameState.getBoard().getHeight();

        weightMatrix = DoubleMatrix.uninitializedMatrix(width, height, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(width, height);
        lastFood = gameState.getBoard().getFood();
        snakeMovePredictor = new SnakeMovePredictor();
        lastMove = UP;

        initialized = true;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();
        freeSpaceMatrix.empty();

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
        final CoordsDto ownHead = gameState.getYou().getHead();
        final int ownSize = gameState.getYou().getLength();
        final String ownId = gameState.getYou().getId();

        boolean updatePredictorOnce = true;
        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final CoordsDto head = snake.getHead();
            final List<CoordsDto> body = snake.getBody();
            final int size = snake.getLength();
            final String id = snake.getId();

            // manage head
            if (!id.equals(ownId)) {
                double baseWeight;
                boolean inedible;

                if (size < ownSize) {
                    baseWeight = snake.getLatency() == 0
                            ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT
                            : LESSER_SNAKE_HEAD_WEIGHT;
                    inedible = false;
                } else {
                    baseWeight = INEDIBLE_SNAKE_HEAD_WEIGHT;
                    inedible = true;
                }

                if (baseWeight != 0.0) {
                    final int x = head.getX();
                    final int y = head.getY();

                    if (Util.manhattanDistance(head, ownHead) > 4) {
                        // cheap and easy on faraway snakes
                        weightMatrix.splash1stOrder(x, y, baseWeight);
                    } else {
                        if (updatePredictorOnce) {
                            snakeMovePredictor.setGameState(gameState);
                            updatePredictorOnce = false;
                        }

                        // spread hunt/danger weights
                        List<KeyValue<CoordsDto, Double>> predictions = snakeMovePredictor.predict(snake);
                        predictions.forEach(prediction -> {
                            final int px = prediction.getKey().getX();
                            final int py = prediction.getKey().getY();
                            final double pw = baseWeight * prediction.getValue();

                            weightMatrix.splash2ndOrder(px, py, pw, 4.0);

                            if (inedible && prediction.getValue() >= BLOCK_INEDIBLE_HEAD_PROBABILITY) {
                                freeSpaceMatrix.setOccupied(px, py);
                            }
                        });
                    }
                }
            }

            // always last - mark body as impassable
            // some cases allow for passing through tail
            int tailMoveOffset = 1;

            // check if fed this turn
            if (lastFood.indexOf(head) >= 0) {
                // tail will grow -> cell will remain occupied
                tailMoveOffset = 0;
            }

            // check if self, and don't do 180* turn
            if (id.equals(ownId) && size == 2) {
                tailMoveOffset = 0;
            }

            for (int i = 0; i < body.size() - tailMoveOffset; ++i) {
                CoordsDto coordsDto = body.get(i);
                final int x = coordsDto.getX();
                final int y = coordsDto.getY();

                weightMatrix.setValue(x, y, SNAKE_BODY_WEIGHT);
                freeSpaceMatrix.setOccupied(x, y);
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

    private List<Triplet<String, Integer, Integer>> rank(List<Triplet<String, Integer, Integer>> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action
        // sort by weight of following actions
        return toRank
                .stream().filter(
                        triplet -> freeSpaceMatrix.getSpace(triplet.getValue1(), triplet.getValue2()) > 0)
                .sorted(Comparator
                        .comparingInt((Triplet<String, Integer, Integer> triplet) -> Math.min(length + 1,
                                freeSpaceMatrix.getSpace(triplet.getValue1(), triplet.getValue2())))
                        .thenComparingDouble((Triplet<String, Integer, Integer> triplet) -> weightMatrix
                                .getValue(triplet.getValue1(), triplet.getValue2()))
                        .thenComparingDouble((Triplet<String, Integer, Integer> triplet) -> getCrossWeight(
                                triplet.getValue1(), triplet.getValue2()))
                        .reversed())
                .collect(Collectors.toList());
    }

    protected String bestMove(CoordsDto head, int length) {
        final int x = head.getX();
        final int y = head.getY();

        List<Triplet<String, Integer, Integer>> ranked = new LinkedList<>();
        ranked.add(new Triplet<>(DOWN, x, y - 1));
        ranked.add(new Triplet<>(LEFT, x - 1, y));
        ranked.add(new Triplet<>(RIGHT, x + 1, y));
        ranked.add(new Triplet<>(UP, x, y + 1));

        ranked = rank(ranked, length);

        if (ranked.isEmpty())
            return lastMove;

        return ranked.get(0).getValue0();
    }

    protected String makeMove(GameStateDto gameState) {
        applyGameState(gameState);
        return bestMove(gameState.getYou().getHead(), gameState.getYou().getLength());
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
        // important to update post decision making to preserver previous move during
        lastFood = gameState.getBoard().getFood();
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
