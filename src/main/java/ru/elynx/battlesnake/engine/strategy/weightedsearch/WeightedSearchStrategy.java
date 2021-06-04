package ru.elynx.battlesnake.engine.strategy.weightedsearch;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.*;
import java.util.function.Supplier;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.SnakeMovePredictor;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class WeightedSearchStrategy implements IGameStrategy, IPredictorInformant {
    private static final double WALL_WEIGHT = 0.0d;

    private static final double MIN_FOOD_WEIGHT = 0.1d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double HUNGER_HEALTH_THRESHOLD = 100.0d;

    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double INEDIBLE_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double BLOCK_NOT_WALKABLE_HEAD_PROBABILITY = Math.nextDown(0.75d);

    private static final double DETERRENT_WEIGHT = -100.0d;

    protected DoubleMatrix weightMatrix;
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected SnakeMovePredictor snakeMovePredictor;

    protected WeightedSearchStrategy() {
    }

    protected void applyHunger(HazardPredictor hazardPredictor) {
        final double foodWeight = Util.scale(MIN_FOOD_WEIGHT,
                HUNGER_HEALTH_THRESHOLD - hazardPredictor.getGameState().getYou().getHealth(), HUNGER_HEALTH_THRESHOLD,
                MAX_FOOD_WEIGHT);

        if (foodWeight <= 0.0)
            return;

        for (Coordinates food : hazardPredictor.getGameState().getBoard().getFood()) {
            weightMatrix.splash2ndOrder(food, foodWeight);
        }
    }

    protected void applySnakes(HazardPredictor hazardPredictor) {
        // mark body as impassable
        // apply early for predictor
        GameState gameState = hazardPredictor.getGameState();
        Common.forAllSnakeBodies(gameState, coordinates -> {
            weightMatrix.addValue(coordinates, SNAKE_BODY_WEIGHT);
            freeSpaceMatrix.setOccupied(coordinates);
        });

        final List<Pair<Coordinates, Double>> blockedByNotWalkable = new LinkedList<>();

        final String ownId = hazardPredictor.getGameState().getYou().getId();
        final Coordinates ownHead = hazardPredictor.getGameState().getYou().getHead();
        final int ownSize = hazardPredictor.getGameState().getYou().getLength();

        for (Snake snake : hazardPredictor.getGameState().getBoard().getSnakes()) {
            final String id = snake.getId();

            // manage head
            if (!id.equals(ownId)) {
                final Coordinates head = snake.getHead();
                final int size = snake.getLength();

                double baseWeight;
                boolean edible;

                if (size < ownSize) {
                    baseWeight = snake.isTimedOut() ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT : LESSER_SNAKE_HEAD_WEIGHT;
                    edible = true;
                } else {
                    baseWeight = INEDIBLE_SNAKE_HEAD_WEIGHT;
                    edible = false;
                }

                if (baseWeight != 0.0) {
                    if (head.manhattanDistance(ownHead) > 4) {
                        // cheap and easy on faraway snakes
                        weightMatrix.splash1stOrder(head, baseWeight);
                    } else {
                        // spread hunt/danger weights
                        List<Pair<Coordinates, Double>> predictions = snakeMovePredictor.predict(snake, gameState);

                        predictions.forEach(prediction -> {
                            Coordinates pc = prediction.getValue0();
                            double pv = prediction.getValue1();
                            double pw = baseWeight * pv;

                            weightMatrix.splash2ndOrder(pc, pw, 4.0);

                            if (pv >= BLOCK_NOT_WALKABLE_HEAD_PROBABILITY) {
                                // edible means preliminary walkable
                                boolean walkable = edible;

                                // if walkable by edibility, see if reachable in single move
                                if (walkable) {
                                    // keep walkable only if next move can eat
                                    walkable = pc.manhattanDistance(ownHead) == 1;
                                }

                                if (!walkable) {
                                    blockedByNotWalkable.add(prediction);
                                }
                            }
                        });
                    }
                }
            }
        }

        for (Pair<Coordinates, Double> blocked : blockedByNotWalkable) {
            Coordinates bc = blocked.getValue0();
            double bv = blocked.getValue1();
            double bw = SNAKE_BODY_WEIGHT * bv;

            weightMatrix.addValue(bc, bw);
            freeSpaceMatrix.setOccupied(bc);
        }
    }

    protected void applyHazards(HazardPredictor hazardPredictor) {
        for (Coordinates hazard : hazardPredictor.getGameState().getBoard().getHazards()) {
            weightMatrix.addValue(hazard, DETERRENT_WEIGHT);
        }

        for (Triplet<Integer, Integer, Double> prediction : hazardPredictor.getPredictedHazards()) {
            final int x = prediction.getValue0();
            final int y = prediction.getValue1();

            // TODO typing
            Coordinates temp = new Coordinates(x, y);

            final double pv = prediction.getValue2();
            final double pw = DETERRENT_WEIGHT * pv;

            weightMatrix.addValue(temp, pw);
        }
    }

    protected void applyGameState(HazardPredictor gameState) {
        weightMatrix.zero();
        freeSpaceMatrix.empty();

        applyHunger(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    protected double getCrossWeight(Coordinates coordinates) {
        double result = weightMatrix.getValue(coordinates);
        for (Coordinates neighbour : coordinates.sideNeighbours()) {
            result += weightMatrix.getValue(neighbour);
        }
        return result;
    }

    protected double getOpportunitiesWeight(MoveCommand moveCommand, Coordinates coordinates) {
        // TODO all of this
        int x = coordinates.getX();
        int y = coordinates.getY();

        int x0;
        int x1;
        int y0;
        int y1;

        switch (moveCommand) {
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
                double weight = weightMatrix.getValue(new Coordinates(xi, yi)); // TODO typing
                // decrease penalties, they will be handled on approach
                if (weight < 0.0) {
                    weight = weight / 10.0;
                }
                opportunities += weight;
            }
        }

        return opportunities;
    }

    protected Optional<MoveCommand> rank(List<Triplet<MoveCommand, Integer, Integer>> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action
        // sort by weight of following actions
        // sort by weight of opportunities
        // sort by reversed comparator, since bigger weight means better solution
        return toRank.stream().filter(
                triplet -> freeSpaceMatrix.isFree(new Coordinates(triplet.getValue1(), triplet.getValue2()))).sorted(
                        Comparator
                                .comparingInt((Triplet<MoveCommand, Integer, Integer> triplet) -> Math.min(length + 1,
                                        freeSpaceMatrix.getFreeSpace(
                                                new Coordinates(triplet.getValue1(), triplet.getValue2()))))
                                .thenComparingDouble(triplet -> weightMatrix
                                        .getValue(new Coordinates(triplet.getValue1(), triplet.getValue2())))
                                .thenComparingDouble(triplet -> getCrossWeight(
                                        new Coordinates(triplet.getValue1(), triplet.getValue2())))
                                .thenComparingDouble(triplet -> getOpportunitiesWeight(triplet.getValue0(),
                                        new Coordinates(triplet.getValue1(), triplet.getValue2())))
                                .reversed())
                .map(Triplet::getValue0).findFirst();
    }

    public Optional<MoveCommand> bestMove(HazardPredictor hazardPredictor) {
        Coordinates head = hazardPredictor.getGameState().getYou().getHead();
        int length = hazardPredictor.getGameState().getYou().getLength();

        final int x = head.getX();
        final int y = head.getY();

        List<Triplet<MoveCommand, Integer, Integer>> ranked = new LinkedList<>();
        ranked.add(new Triplet<>(DOWN, x, y - 1));
        ranked.add(new Triplet<>(LEFT, x - 1, y));
        ranked.add(new Triplet<>(RIGHT, x + 1, y));
        ranked.add(new Triplet<>(UP, x, y + 1));

        return rank(ranked, length);
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#50c878", "smile", "sharp", "1a");
    }

    @Override
    public void init(HazardPredictor hazardPredictor) {
        Dimensions dimensions = hazardPredictor.getGameState().getBoard().getDimensions();

        weightMatrix = DoubleMatrix.uninitializedMatrix(dimensions, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(dimensions);
        snakeMovePredictor = new SnakeMovePredictor(this);
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        applyGameState(hazardPredictor);
        Optional<MoveCommand> move = bestMove(hazardPredictor);

        if (move.isEmpty()) {
            // TODO remove null
            return new Move(MoveCommand.REPEAT_LAST, null); // would repeat last turn
        }

        return new Move(move.get(), null); // TODO remove null
    }

    @Override
    public Void processEnd(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public boolean isWalkable(Coordinates coordinates) {
        return freeSpaceMatrix.isFree(coordinates);
    }

    @Configuration
    public static class WeightedSearchStrategyConfiguration {
        @Bean("Ahaetulla")
        public Supplier<IGameStrategy> weightedSearch() {
            return WeightedSearchStrategy::new;
        }
    }
}
