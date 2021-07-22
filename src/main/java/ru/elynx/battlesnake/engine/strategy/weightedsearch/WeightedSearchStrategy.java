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
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class WeightedSearchStrategy implements IPolySnakeGameStrategy, IPredictorInformant {
    private static final double WALL_WEIGHT = 0.0d;

    private static final double MIN_FOOD_WEIGHT = 0.2d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;

    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double INEDIBLE_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double BLOCK_NOT_WALKABLE_HEAD_PROBABILITY = Math.nextDown(0.75d);

    private static final double DETERRENT_WEIGHT = -100.0d;

    private DoubleMatrix weightMatrix;
    private FreeSpaceMatrix freeSpaceMatrix;
    private SnakeMovePredictor snakeMovePredictor;
    private Map<Coordinates, Integer> lesserSnakeHeads;

    private String primarySnakeId = null;

    private void applyFood(Snake snake, GameState gameState) {
        int healthGainedFromFood = Snake.getMaxHealth() - snake.getHealth();
        double foodWeight = Util.scale(MIN_FOOD_WEIGHT, healthGainedFromFood, Snake.getMaxHealth(), MAX_FOOD_WEIGHT);

        List<Coordinates> hazards = gameState.getBoard().getHazards();
        boolean headInHazard = hazards.contains(snake.getHead());

        List<Coordinates> foods = gameState.getBoard().getFood();

        if (headInHazard) {
            for (Coordinates food : foods) {
                weightMatrix.splash2ndOrder(food, foodWeight);
            }
        } else {
            // if not in hazard, do not smell from hazard
            for (Coordinates food : foods) {
                if (hazards.contains(food)) {
                    weightMatrix.addValue(food, foodWeight);
                } else {
                    weightMatrix.splash2ndOrder(food, foodWeight);
                }
            }
        }
    }

    private void applySnakes(Snake snake, GameState gameState) {
        // mark body as impassable
        // apply early for predictor
        Common.forAllSnakeBodies(gameState, coordinates -> {
            weightMatrix.addValue(coordinates, SNAKE_BODY_WEIGHT);
            freeSpaceMatrix.setOccupied(coordinates);
        });

        List<Pair<Coordinates, Double>> blockedByNotWalkable = new LinkedList<>();

        String ownId = snake.getId();
        Coordinates ownHead = snake.getHead();
        int ownSize = snake.getLength();

        for (Snake someSnake : gameState.getBoard().getSnakes()) {
            String id = someSnake.getId();

            // manage head
            if (!id.equals(ownId)) {
                Coordinates head = someSnake.getHead();
                int size = someSnake.getLength();

                double baseWeight;
                boolean edible;

                if (size < ownSize) {
                    baseWeight = someSnake.isTimedOut() ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT : LESSER_SNAKE_HEAD_WEIGHT;
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
                        boolean isPrimarySnake = id.equals(primarySnakeId);

                        if (isPrimarySnake) {
                            weightMatrix.splash1stOrder(head, baseWeight);
                        }

                        // spread hunt/danger weights
                        List<Pair<Coordinates, Double>> predictions = snakeMovePredictor.predict(someSnake, gameState);

                        predictions.forEach(prediction -> {
                            Coordinates pc = prediction.getValue0();
                            double pv = prediction.getValue1();

                            if (!isPrimarySnake) {
                                double pw = baseWeight * pv;
                                weightMatrix.splash2ndOrder(pc, pw, 4.0d);
                            }

                            if (pv >= BLOCK_NOT_WALKABLE_HEAD_PROBABILITY) {
                                // edible means preliminary walkable
                                boolean walkable = edible;

                                // if walkable by edibility, see if reachable in single move
                                if (walkable) {
                                    // keep walkable only if next move can eat
                                    walkable = pc.manhattanDistance(ownHead) == 1;
                                }

                                if (!walkable && !isPrimarySnake) {
                                    blockedByNotWalkable.add(prediction);
                                }

                                if (edible && walkable) {
                                    lesserSnakeHeads.put(pc, someSnake.getLength());
                                }
                            }
                        });
                    }
                }
            }
        }

        for (Pair<Coordinates, Double> blocked : blockedByNotWalkable) {
            Coordinates bc = blocked.getValue0();
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

    private void applyGameState(Snake snake, GameState gameState) {
        weightMatrix.zero();
        freeSpaceMatrix.empty();
        lesserSnakeHeads.clear();

        applyFood(snake, gameState);
        applySnakes(snake, gameState);
        applyHazards(gameState);
    }

    private int getBoundedFreeSpace(int length, Coordinates coordinates) {
        int fromEating = lesserSnakeHeads.getOrDefault(coordinates, 0);
        int fromMatrix = freeSpaceMatrix.getFreeSpace(coordinates);
        int freeSpace = fromEating + fromMatrix;

        int bound = length + 1;

        return Math.min(bound, freeSpace);
    }

    private double getImmediateWeight(Coordinates coordinates) {
        return weightMatrix.getValue(coordinates);
    }

    private double getCrossWeight(Coordinates coordinates) {
        double result = weightMatrix.getValue(coordinates);
        int items = 0;
        for (Coordinates neighbour : coordinates.sideNeighbours()) {
            if (isInsideBounds(neighbour)) {
                result += weightMatrix.getValue(neighbour);
                ++items;
            }
        }

        if (items == 0) {
            return 0.0d;
        }

        return result / items;
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
        int items = 0;

        // in array access friendly order
        for (int yi = y0; yi <= y1; ++yi) {
            for (int xi = x0; xi <= x1; ++xi) {
                if (isInsideBounds(xi, yi)) {
                    double weight = weightMatrix.getValue(xi, yi);
                    // decrease penalties, they will be handled on approach
                    if (weight < 0.0d) {
                        weight = weight / 10.0d;
                    }
                    opportunities += weight;
                    ++items;
                }
            }
        }

        if (items == 0) {
            return 0.0d;
        }

        return opportunities / items;
    }

    private Optional<MoveCommand> rank(Collection<CoordinatesWithDirection> toRank, int length, MoveCommand toIgnore) {
        // filter all that go outside of map or step on occupied cell
        // sort by provided freedom of movement, capped at length + 1 for more options
        // sort by weight of immediate action
        // sort by weight of following actions
        // sort by weight of opportunities
        return toRank.stream().filter(x -> !x.getDirection().equals(toIgnore)).filter(this::isWalkable).max(Comparator
                .comparingInt((CoordinatesWithDirection coordinates) -> getBoundedFreeSpace(length, coordinates))
                .thenComparingDouble(this::getImmediateWeight).thenComparingDouble(this::getCrossWeight)
                .thenComparingDouble(this::getOpportunitiesWeight)).map(CoordinatesWithDirection::getDirection);
    }

    Optional<MoveCommand> backupMove(Snake snake) {
        return snake.getAdvancingMoves().stream().filter(this::isInsideBounds)
                .max(Comparator.comparingDouble(this::getImmediateWeight)).map(CoordinatesWithDirection::getDirection);
    }

    private boolean isInsideBounds(Coordinates x) {
        return !weightMatrix.getDimensions().isOutOfBounds(x);
    }

    private boolean isInsideBounds(int x, int y) {
        return !weightMatrix.getDimensions().isOutOfBounds(x, y);
    }

    private Optional<MoveCommand> bestMove(Snake snake, MoveCommand toIgnore) {
        Collection<CoordinatesWithDirection> ranked = snake.getAdvancingMoves();
        int length = snake.getLength();
        return rank(ranked, length, toIgnore);
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
        lesserSnakeHeads = new HashMap<>(gameState.getBoard().getSnakes().size());
    }

    @Override
    public void setPrimarySnake(Snake snake) {
        primarySnakeId = snake.getId();
    }

    @Override
    public Optional<MoveCommand> processMove(Snake snake, GameState gameState) {
        return processMoveImpl(snake, gameState);
    }

    private Optional<MoveCommand> processMoveImpl(Snake snake, GameState gameState) {
        applyGameState(snake, gameState);
        return bestMove(snake, null).or(() -> backupMove(snake));
    }

    @Override
    public List<MoveCommandWithProbability> evaluateMoves(Snake snake, GameState gameState) {
        if (needSpecialHandling(snake, gameState)) {
            return detailedEvaluation(snake, gameState);
        }

        return singleBestMove(snake, gameState);
    }

    private boolean needSpecialHandling(Snake snake, GameState gameState) {
        // on first turn all snakes have 4 moves, this will lead to explosion of options
        // since first move is important, use safe option
        if (gameState.getTurn() == 1) {
            return false;
        }

        if (primarySnakeId != null) {
            Optional<Snake> primarySnakeOptional = gameState.getBoard().getSnakes().stream()
                    .filter(x -> x.getId().equals(primarySnakeId)).findAny();

            if (primarySnakeOptional.isPresent()) {
                Snake primarySnake = primarySnakeOptional.get();
                return snake.getHead().manhattanDistance(primarySnake.getHead()) == 2;
            }
        }

        return false;
    }

    private List<MoveCommandWithProbability> singleBestMove(Snake snake, GameState gameState) {
        Optional<MoveCommand> move = processMoveImpl(snake, gameState);
        if (move.isEmpty()) {
            return Collections.emptyList();
        }

        return MoveCommandWithProbability.onlyFrom(move.get());
    }

    private List<MoveCommandWithProbability> detailedEvaluation(Snake snake, GameState gameState) {
        double sigma = Math.nextUp(0.0d);
        double countersink = 0.05d;

        applyGameState(snake, gameState);

        Optional<MoveCommand> move1 = bestMove(snake, null);
        if (move1.isEmpty()) {
            Optional<MoveCommand> backupMove = backupMove(snake);
            if (backupMove.isEmpty()) {
                return Collections.emptyList();
            }
            return MoveCommandWithProbability.onlyFrom(backupMove.get());
        }

        Optional<MoveCommand> move2 = bestMove(snake, move1.get());
        if (move2.isEmpty()) {
            return MoveCommandWithProbability.onlyFrom(move1.get());
        }

        CoordinatesWithDirection c1 = snake.getHead().move(move1.get());
        CoordinatesWithDirection c2 = snake.getHead().move(move2.get());

        double w1 = getImmediateWeight(c1);
        double w2 = getImmediateWeight(c2);

        if (Math.abs(w1 - w2) <= sigma) {
            double wHead = getImmediateWeight(snake.getHead());
            w1 = getCrossWeight(c1) - wHead;
            w2 = getCrossWeight(c2) - wHead;

            if (Math.abs(w1 - w2) <= sigma) {
                w1 = getOpportunitiesWeight(c1);
                w2 = getOpportunitiesWeight(c2);
            }
        }

        if (w1 <= 0.0d) {
            w2 = w2 - w1 + countersink;
            w1 = countersink;
        }

        if (w2 <= 0.0d) {
            w1 = w1 - w2 + countersink;
            w2 = countersink;
        }

        double wSum = w1 + w2;
        return List.of(new MoveCommandWithProbability(move1.get(), w1 / wSum),
                new MoveCommandWithProbability(move2.get(), w2 / wSum));
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
