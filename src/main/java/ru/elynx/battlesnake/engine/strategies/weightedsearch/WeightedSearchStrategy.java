package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.*;
import java.util.function.Supplier;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.SnakeMovePredictor;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchStrategy implements IGameStrategy, IPredictorInformant {
    private static final double WALL_WEIGHT = 0.0d;

    private static final double MIN_FOOD_WEIGHT = 0.0d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double HUNGER_HEALTH_THRESHOLD = 100.0d;

    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double INEDIBLE_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double BLOCK_INEDIBLE_HEAD_PROBABILITY = 0.85d;

    private static final double DETERRENT_WEIGHT = -100.0d;

    protected DoubleMatrix weightMatrix;
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected SnakeMovePredictor snakeMovePredictor;

    protected boolean initialized = false;

    protected WeightedSearchStrategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        final int width = gameState.getBoard().getWidth();
        final int height = gameState.getBoard().getHeight();

        weightMatrix = DoubleMatrix.uninitializedMatrix(width, height, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(width, height);
        snakeMovePredictor = new SnakeMovePredictor(this);
    }

    protected void applyHunger(GameStatePredictor gameState) {
        final double foodWeight = Util.scale(MIN_FOOD_WEIGHT, HUNGER_HEALTH_THRESHOLD - gameState.getYou().getHealth(),
                HUNGER_HEALTH_THRESHOLD, MAX_FOOD_WEIGHT);

        if (foodWeight <= 0.0)
            return;

        for (CoordsDto food : gameState.getBoard().getFood()) {
            final int x = food.getX();
            final int y = food.getY();

            weightMatrix.splash2ndOrder(x, y, foodWeight);
        }
    }

    protected void applySnakes(GameStatePredictor gameState) {
        final CoordsDto ownHead = gameState.getYou().getHead();
        final String ownId = gameState.getYou().getId();

        // mark body as impassable
        // apply early for predictor
        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();

            // by default tail will go away
            int tailMoveOffset = 1;

            // check if fed this turn
            if (gameState.isGrowing(snake)) {
                // tail will grow -> cell will remain occupied
                tailMoveOffset = 0;
            }

            for (int i = 0; i < body.size() - tailMoveOffset; ++i) {
                final CoordsDto coordsDto = body.get(i);
                final int x = coordsDto.getX();
                final int y = coordsDto.getY();

                weightMatrix.setValue(x, y, SNAKE_BODY_WEIGHT);
                freeSpaceMatrix.setOccupied(x, y);
            }
        }

        final List<Triplet<Integer, Integer, Double>> blockedByInedible = new LinkedList<>();
        final int ownSize = gameState.getYou().getLength();
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
                        // spread hunt/danger weights
                        List<Triplet<Integer, Integer, Double>> predictions = snakeMovePredictor.predict(snake);
                        predictions.forEach(prediction -> {
                            final int px = prediction.getValue0();
                            final int py = prediction.getValue1();
                            final double pv = prediction.getValue2();
                            final double pw = baseWeight * pv;

                            weightMatrix.splash2ndOrder(px, py, pw, 4.0);

                            if (inedible && pv >= BLOCK_INEDIBLE_HEAD_PROBABILITY) {
                                blockedByInedible.add(prediction);
                            }
                        });
                    }
                }
            }
        }

        for (Triplet<Integer, Integer, Double> triplet : blockedByInedible) {
            freeSpaceMatrix.setOccupied(triplet.getValue0(), triplet.getValue1());
        }
    }

    protected void applyHazards(GameStatePredictor gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, DETERRENT_WEIGHT);
        }

        for (Triplet<Integer, Integer, Double> prediction : gameState.getPredictedHazards()) {
            final int x = prediction.getValue0();
            final int y = prediction.getValue1();
            final double pv = prediction.getValue2();
            final double pw = DETERRENT_WEIGHT * pv;

            weightMatrix.setValue(x, y, pw);
        }
    }

    protected void applyGameState(GameStatePredictor gameState) {
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

    protected double getOpportunitiesWeight(String direction, int x, int y) {
        int x0;
        int x1;
        int y0;
        int y1;

        switch (direction) {
            case DOWN :
                x0 = x - 2;
                x1 = x + 2;
                y0 = y - 3;
                y1 = y;
                break;
            case LEFT :
                x0 = x - 3;
                x1 = x;
                y0 = y - 2;
                y1 = y + 2;
                break;
            case RIGHT :
                x0 = x;
                x1 = x + 3;
                y0 = y - 2;
                y1 = y + 2;
                break;
            case UP :
                x0 = x - 2;
                x1 = x + 2;
                y0 = y;
                y1 = y + 3;
                break;
            default :
                return DETERRENT_WEIGHT; // don't throw in the middle of the move
        }

        double opportunities = 0.0;
        // in array access friendly order
        for (int yi = y0; yi <= y1; ++yi) {
            for (int xi = x0; xi <= x1; ++xi) {
                double weight = weightMatrix.getValue(xi, yi);
                // decrease penalties, they will be handled on approach
                if (weight < 0.0) {
                    weight = weight / 10.0;
                }
                opportunities += weight;
            }
        }

        return opportunities;
    }

    protected Optional<String> rank(List<Triplet<String, Integer, Integer>> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action
        // sort by weight of following actions
        // sort by weight of opportunities
        // sort by reversed comparator, since bigger weight means better solution
        return toRank
                .stream().filter(
                        triplet -> freeSpaceMatrix.getSpace(triplet.getValue1(), triplet.getValue2()) > 0)
                .sorted(Comparator
                        .comparingInt((Triplet<String, Integer, Integer> triplet) -> Math.min(length + 1,
                                freeSpaceMatrix.getSpace(triplet.getValue1(), triplet.getValue2())))
                        .thenComparingDouble(triplet -> weightMatrix.getValue(triplet.getValue1(), triplet.getValue2()))
                        .thenComparingDouble(triplet -> getCrossWeight(triplet.getValue1(), triplet.getValue2()))
                        .thenComparingDouble(triplet -> getOpportunitiesWeight(triplet.getValue0(), triplet.getValue1(),
                                triplet.getValue2()))
                        .reversed())
                .map(Triplet::getValue0).findFirst();
    }

    public Optional<String> bestMove(GameStateDto gameState) {
        final CoordsDto head = gameState.getYou().getHead();
        final int length = gameState.getYou().getLength();

        final int x = head.getX();
        final int y = head.getY();

        List<Triplet<String, Integer, Integer>> ranked = new LinkedList<>();
        ranked.add(new Triplet<>(DOWN, x, y - 1));
        ranked.add(new Triplet<>(LEFT, x - 1, y));
        ranked.add(new Triplet<>(RIGHT, x + 1, y));
        ranked.add(new Triplet<>(UP, x, y + 1));

        return rank(ranked, length);
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ef9600", "smile", "sharp", "1a");
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
    public Move processMove(GameStateDto gameState) {
        // for test compatibility
        if (!initialized) {
            initOnce(gameState);
            initialized = true;
        }

        applyGameState((GameStatePredictor) gameState);
        Optional<String> move = bestMove(gameState);

        if (move.isEmpty()) {
            return new Move(); // would repeat last turn
        }

        return new Move(move.get());
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Override
    public boolean isWalkable(int x, int y) {
        return freeSpaceMatrix.getSpace(x, y) > 0;
    }

    @Configuration
    public static class WeightedSearchStrategyConfiguration {
        @Bean("Snake_1a")
        public Supplier<IGameStrategy> weightedSearch() {
            return WeightedSearchStrategy::new;
        }
    }
}
