package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.javatuples.KeyValue;
import org.javatuples.Quartet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.strategies.shared.IMetaEnabledGameStrategy;
import ru.elynx.battlesnake.engine.strategies.shared.SnakeMovePredictor;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchV2Strategy implements IGameStrategy, IMetaEnabledGameStrategy {
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

    // reset each processMove independent of meta
    protected DoubleMatrix weightMatrix;
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected SnakeMovePredictor snakeMovePredictor;

    // affected by normal processing
    protected String lastMove;

    // affected by normal and meta processing
    protected List<CoordsDto> lastFood;

    protected boolean initialized = false;

    protected WeightedSearchV2Strategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        final int width = gameState.getBoard().getWidth();
        final int height = gameState.getBoard().getHeight();

        weightMatrix = DoubleMatrix.uninitializedMatrix(width, height, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(width, height);
        snakeMovePredictor = new SnakeMovePredictor();

        lastFood = gameState.getBoard().getFood();
        lastMove = UP;
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
        final String ownId = gameState.getYou().getId();

        // mark body as impassable
        // apply early for predictor
        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final CoordsDto head = snake.getHead();
            final String id = snake.getId();

            final List<CoordsDto> body = snake.getBody();

            // some cases allow for passing through tail
            int tailMoveOffset = 1;

            // check if fed this turn
            if (lastFood.indexOf(head) >= 0) {
                // tail will grow -> cell will remain occupied
                tailMoveOffset = 0;
            }

            // check if self, and don't do 180* turn
            if (id.equals(ownId) && body.size() == 2) {
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

        List<CoordsDto> blockedByInedible = new LinkedList<>();
        final int ownSize = gameState.getYou().getLength();
        boolean updatePredictorOnce = true;
        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final CoordsDto head = snake.getHead();
            final String id = snake.getId();

            final List<CoordsDto> body = snake.getBody();
            final int size = body.size();

            // manage head
            if (!id.equals(ownId)) {
                double baseWeight;
                boolean inedible;

                if (size < ownSize) {
                    baseWeight = snake.isTimedOut() ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT : LESSER_SNAKE_HEAD_WEIGHT;
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
                            snakeMovePredictor.setFreeSpace(freeSpaceMatrix);
                            updatePredictorOnce = false;
                        }

                        // spread hunt/danger weights
                        List<KeyValue<CoordsDto, Double>> predictions = snakeMovePredictor.predict(snake);
                        predictions.forEach(prediction -> {
                            final CoordsDto coords = prediction.getKey();
                            final int px = coords.getX();
                            final int py = coords.getY();
                            final double pv = prediction.getValue();
                            final double pw = baseWeight * pv;

                            weightMatrix.splash2ndOrder(px, py, pw, 4.0);

                            if (inedible && pv >= BLOCK_INEDIBLE_HEAD_PROBABILITY) {
                                blockedByInedible.add(coords);
                            }
                        });
                    }
                }
            }
        }

        for (CoordsDto coords : blockedByInedible) {
            freeSpaceMatrix.setOccupied(coords.getX(), coords.getY());
        }
    }

    protected void applyHazards(GameStateDto gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, DETERRENT_WEIGHT);
        }
    }

    protected void applyGameState(GameStateDto gameState) {
        // for test compatibility
        if (!initialized) {
            initOnce(gameState);
            initialized = true;
        }

        weightMatrix.zero();
        freeSpaceMatrix.empty();

        applyHunger(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    protected double getCrossWeight(int x, int y) {
        double result = weightMatrix.getValue(x, y - 1);
        result += weightMatrix.getValue(x - 1, y);
        result += weightMatrix.getValue(x, y);
        result += weightMatrix.getValue(x + 1, y);
        result += weightMatrix.getValue(x, y + 1);
        return result;
    }

    protected List<Quartet<String, Integer, Integer, Double>> rank(
            List<Quartet<String, Integer, Integer, Double>> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // get weight of immediate action, and store for later use in meta and sort
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action (stored)
        // sort by weight of following actions
        return toRank.stream().filter(quartet -> freeSpaceMatrix.getSpace(quartet.getValue1(), quartet.getValue2()) > 0)
                .map(quartet -> quartet.setAt3(weightMatrix.getValue(quartet.getValue1(), quartet.getValue2())))
                .sorted(Comparator
                        .comparingInt((Quartet<String, Integer, Integer, Double> quartet) -> Math.min(length + 1,
                                freeSpaceMatrix.getSpace(quartet.getValue1(), quartet.getValue2())))
                        .thenComparingDouble(Quartet::getValue3)
                        .thenComparingDouble(quartet -> getCrossWeight(quartet.getValue1(), quartet.getValue2()))
                        .reversed())
                .collect(Collectors.toList());
    }

    public List<Quartet<String, Integer, Integer, Double>> bestMove(GameStateDto gameState) {
        final CoordsDto head = gameState.getYou().getHead();
        final int length = gameState.getYou().getLength();

        final int x = head.getX();
        final int y = head.getY();

        List<Quartet<String, Integer, Integer, Double>> ranked = new LinkedList<>();
        ranked.add(new Quartet<>(DOWN, x, y - 1, 0.0));
        ranked.add(new Quartet<>(LEFT, x - 1, y, 0.0));
        ranked.add(new Quartet<>(RIGHT, x + 1, y, 0.0));
        ranked.add(new Quartet<>(UP, x, y + 1, 0.0));

        return rank(ranked, length);
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ef9600", "smile", "sharp", "1a X");
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        if (!initialized) {
            initOnce(gameState);
            initialized = true;
        }

        return null;
    }

    @Override
    public void setLastMove(GameStateDto gameStateDto) {
        lastFood = gameStateDto.getBoard().getFood();
    }

    @Override
    public List<Quartet<String, Integer, Integer, Double>> processMoveMeta(GameStateDto gameState) {
        applyGameState(gameState);

        List<Quartet<String, Integer, Integer, Double>> moves = bestMove(gameState);

        lastFood = gameState.getBoard().getFood();

        return moves;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        List<Quartet<String, Integer, Integer, Double>> moves = processMoveMeta(gameState);

        if (!moves.isEmpty()) {
            lastMove = moves.get(0).getValue0();
        }

        return new Move(lastMove, "New and theoretically improved");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class WeightedSearchV2StrategyConfiguration {
        @Bean("Snake_1a")
        public Supplier<IGameStrategy> weightedSearch() {
            return WeightedSearchV2Strategy::new;
        }

        public static Supplier<WeightedSearchV2Strategy> weightedSearchMeta() {
            return WeightedSearchV2Strategy::new;
        }
    }
}
