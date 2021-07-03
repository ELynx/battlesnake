package ru.elynx.battlesnake.engine.strategy.weightedsearch;

import java.util.*;
import java.util.function.Supplier;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.math.Util;
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

    private DoubleMatrix weightMatrix;
    private FreeSpaceMatrix freeSpaceMatrix;
    private SnakeMovePredictor snakeMovePredictor;

    private WeightedSearchStrategy() {
    }

    private void applyFood(GameState gameState) {
        double foodWeight = Util.scale(MIN_FOOD_WEIGHT, HUNGER_HEALTH_THRESHOLD - gameState.getYou().getHealth(),
                HUNGER_HEALTH_THRESHOLD, MAX_FOOD_WEIGHT);

        if (foodWeight <= 0.0d)
            return;

        for (Coordinates food : gameState.getBoard().getFood()) {
            weightMatrix.splash2ndOrder(food, foodWeight);
        }
    }

    private void applySnakes(GameState gameState) {
        // mark body as impassable
        // apply early for predictor
        Common.forAllSnakeBodies(gameState, coordinates -> {
            weightMatrix.addValue(coordinates, SNAKE_BODY_WEIGHT);
            freeSpaceMatrix.setOccupied(coordinates);
        });

        List<Pair<Coordinates, Double>> blockedByNotWalkable = new LinkedList<>();

        String ownId = gameState.getYou().getId();
        Coordinates ownHead = gameState.getYou().getHead();
        int ownSize = gameState.getYou().getLength();

        for (Snake snake : gameState.getBoard().getSnakes()) {
            String id = snake.getId();

            // manage head
            if (!id.equals(ownId)) {
                Coordinates head = snake.getHead();
                int size = snake.getLength();

                double baseWeight;
                boolean edible;

                if (size < ownSize) {
                    baseWeight = snake.isTimedOut() ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT : LESSER_SNAKE_HEAD_WEIGHT;
                    edible = true;
                } else {
                    baseWeight = INEDIBLE_SNAKE_HEAD_WEIGHT;
                    edible = false;
                }

                if (baseWeight != 0.0d) {
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

                            weightMatrix.splash2ndOrder(pc, pw, 4.0d);

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

    private void applyHazards(GameState gameState) {
        Coordinates center = gameState.getBoard().getDimensions().center();

        for (Coordinates hazard : gameState.getBoard().getHazards()) {
            double w = hazardPositionWeight(center, hazard);
            double ww = DETERRENT_WEIGHT * w;
            weightMatrix.addValue(hazard, ww);
        }
    }

    private double hazardPositionWeight(Coordinates center, Coordinates hazard) {
        int distanceInSteps = center.manhattanDistance(hazard);
        int distanceInStepsFromCorner = center.getX() + center.getY(); // from (0, 0)
        // small gradient will still be detected
        return Util.scale(0.95, distanceInSteps, distanceInStepsFromCorner, 1.0d);
    }

    private void applyGameState(GameState gameState) {
        weightMatrix.zero();
        freeSpaceMatrix.empty();

        applyFood(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    private int getBoundedFreeSpace(int length, Coordinates coordinates) {
        return Math.min(length + 1, freeSpaceMatrix.getFreeSpace(coordinates));
    }

    private double getCrossWeight(Coordinates coordinates) {
        double result = weightMatrix.getValue(coordinates);
        for (Coordinates neighbour : coordinates.sideNeighbours()) {
            result += weightMatrix.getValue(neighbour);
        }
        return result;
    }

    private double getOpportunitiesWeight(CoordinatesWithDirection coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();

        int x0;
        int x1;
        int y0;
        int y1;

        switch (coordinates.getDirection()) {
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

        double opportunities = 0.0d;
        // in array access friendly order
        for (int yi = y0; yi <= y1; ++yi) {
            for (int xi = x0; xi <= x1; ++xi) {
                double weight = weightMatrix.getValue(xi, yi);
                // decrease penalties, they will be handled on approach
                if (weight < 0.0d) {
                    weight = weight / 10.0d;
                }
                opportunities += weight;
            }
        }

        return opportunities;
    }

    private Optional<MoveCommand> rank(Collection<CoordinatesWithDirection> toRank, int length) {
        // filter all that go outside of map or step on occupied cell
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action
        // sort by weight of following actions
        // sort by weight of opportunities
        // sort by reversed comparator, since bigger weight means better solution
        return toRank.stream().filter(this::isWalkable).sorted(Comparator
                .comparingInt((CoordinatesWithDirection coordinates) -> getBoundedFreeSpace(length, coordinates))
                .thenComparingDouble(coordinates -> weightMatrix.getValue(coordinates))
                .thenComparingDouble(this::getCrossWeight).thenComparingDouble(this::getOpportunitiesWeight).reversed())
                .map(CoordinatesWithDirection::getDirection).findFirst();
    }

    public Optional<MoveCommand> bestMove(GameState gameState) {
        Collection<CoordinatesWithDirection> ranked = gameState.getYou().getHead().sideNeighbours();
        int length = gameState.getYou().getLength();
        return rank(ranked, length);
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#50c878", "smile", "sharp", "2");
    }

    @Override
    public void init(GameState gameState) {
        Dimensions dimensions = gameState.getBoard().getDimensions();

        weightMatrix = DoubleMatrix.uninitializedMatrix(dimensions, WALL_WEIGHT);
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(dimensions);
        snakeMovePredictor = new SnakeMovePredictor(this);
    }

    @Override
    public Void processStart(GameState gameState) {
        return null;
    }

    @Override
    public Move processMove(GameState gameState) {
        applyGameState(gameState);
        Optional<MoveCommand> move = bestMove(gameState);

        if (move.isEmpty()) {
            return new Move(MoveCommand.REPEAT_LAST); // would repeat last turn
        }

        return new Move(move.get());
    }

    @Override
    public Void processEnd(GameState gameState) {
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
