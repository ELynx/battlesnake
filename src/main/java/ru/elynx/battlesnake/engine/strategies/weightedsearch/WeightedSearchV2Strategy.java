package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.game.predictor.GameStatePredictor;
import ru.elynx.battlesnake.engine.game.predictor.impl.IPredictorInformant;
import ru.elynx.battlesnake.engine.game.predictor.impl.SnakeMovePredictor;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.strategies.shared.IMetaEnabledGameStrategy;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchV2Strategy implements IGameStrategy, IMetaEnabledGameStrategy, IPredictorInformant {
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
    protected SnakeMovePredictor snakeMovePredictor;

    protected boolean initialized = false;

    protected WeightedSearchV2Strategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        final int width = gameState.getBoard().getWidth();
        final int height = gameState.getBoard().getHeight();

        weightMatrix = DoubleMatrix.uninitializedMatrix(width, height, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(width, height);
        snakeMovePredictor = new SnakeMovePredictor(this);
    }

    protected void applyHunger(GameStateDto gameState) {
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

    protected void applySnakes(GameStateDto gameState) {
        final GameStatePredictor gameStatePredictor = (GameStatePredictor) gameState;

        final CoordsDto ownHead = gameStatePredictor.getYou().getHead();
        final String ownId = gameStatePredictor.getYou().getId();

        // mark body as impassable
        // apply early for predictor
        for (SnakeDto snake : gameStatePredictor.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();

            // some cases allow for passing through tail
            int tailMoveOffset = 1;

            // check if fed this turn
            if (gameStatePredictor.isGrowing(snake)) {
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

    protected void applyHazards(GameStateDto gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, DETERRENT_WEIGHT);
        }
    }

    protected void applyGameState(GameStateDto gameState) {
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

    protected List<Quartet<String, Integer, Integer, Double>> rank(
            List<Quartet<String, Integer, Integer, Double>> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // get weight of immediate action, and store for later use in meta and sort
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action (stored)
        // sort by weight of following actions
        // sort by weight of opportunities
        return toRank.stream().filter(quartet -> freeSpaceMatrix.getSpace(quartet.getValue1(), quartet.getValue2()) > 0)
                .map(quartet -> quartet.setAt3(weightMatrix.getValue(quartet.getValue1(), quartet.getValue2())))
                .sorted(Comparator
                        .comparingInt((Quartet<String, Integer, Integer, Double> quartet) -> Math.min(length + 1,
                                freeSpaceMatrix.getSpace(quartet.getValue1(), quartet.getValue2())))
                        .thenComparingDouble(Quartet::getValue3)
                        .thenComparingDouble(quartet -> getCrossWeight(quartet.getValue1(), quartet.getValue2()))
                        .thenComparingDouble(quartet -> getOpportunitiesWeight(quartet.getValue0(), quartet.getValue1(),
                                quartet.getValue2()))
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
    public List<Quartet<String, Integer, Integer, Double>> processMoveMeta(GameStateDto gameState) {
        // for test compatibility
        if (!initialized) {
            initOnce(gameState);
            initialized = true;
        }

        applyGameState(gameState);
        return bestMove(gameState);
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        List<Quartet<String, Integer, Integer, Double>> moves = processMoveMeta(gameState);

        if (moves.isEmpty()) {
            return new Move(); // would repeat last turn
        }

        return new Move(moves.get(0).getValue0());
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
