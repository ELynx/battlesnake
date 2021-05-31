package ru.elynx.battlesnake.engine.predictor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.api.CoordsDto;
import ru.elynx.battlesnake.api.GameStateDto;
import ru.elynx.battlesnake.api.SnakeDto;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;

public class GameStatePredictor extends GameStateDto {
    private static final int INITIAL_LENGTH = 3;
    private static final int MAX_HEALTH = 100;

    protected int hazardStep = 0;

    private List<Triplet<Integer, Integer, Double>> predictedHazardsCache = null;

    public void setHazardStep(int hazardStep) {
        this.hazardStep = hazardStep;
    }

    /**
     * Predict if snake will grow on it's tail this turn
     *
     * @param snake
     *            Snake to be checked
     * @return True if snake will not empty it's tail cell.
     */
    public boolean isGrowing(SnakeDto snake) {
        // initial expansion
        if (getTurn() < INITIAL_LENGTH) {
            return true;
        }

        // just ate food
        return snake.getHealth().equals(MAX_HEALTH);
    }

    public List<Triplet<Integer, Integer, Double>> getPredictedHazards() {
        if (predictedHazardsCache == null) {
            predictedHazardsCache = getPredictedHazardsImpl();
        }

        return predictedHazardsCache;
    }

    private List<Triplet<Integer, Integer, Double>> getPredictedHazardsImpl() {
        if (hazardStep != 0 && getGame().getRuleset().isRoyale()) {
            final int width = getBoard().getWidth();
            final int height = getBoard().getHeight();

            // if everything is hazard, there is no more prediction
            if (getBoard().getHazards().size() >= width * height) {
                return Collections.emptyList();
            }

            // Go has very deterministic pseudorandom, and only two lower bits are used
            // seed is re-used through game, and business logic is based on repeatability
            // in theory, it is quite possible to determine hazards accurately

            if (getTurn() % hazardStep == hazardStep - 1) {
                FreeSpaceMatrix freeSpaceMatrix = FreeSpaceMatrix.emptyMatrix(width, height);

                for (CoordsDto hazard : getBoard().getHazards()) {
                    freeSpaceMatrix.setOccupied(hazard.getX(), hazard.getY());
                }

                int xMin = -1;
                int xMax = -1;
                int yMin = -1;
                int yMax = -1;

                for (int x = 0; x < width; ++x) {
                    if (yMin == -1 && yMax == -1) {
                        int y;
                        for (y = 0; y < height; ++y)
                            if (freeSpaceMatrix.isFree(x, y)) {
                                yMin = y;
                                break;
                            }

                        for (; y < height; ++y)
                            if (freeSpaceMatrix.isFree(x, y))
                                yMax = y;
                            else
                                break;
                    }

                    if (yMin != -1) {
                        if (xMin == -1)
                            xMin = x;

                        if (freeSpaceMatrix.isFree(x, yMin))
                            xMax = x;
                        else
                            break;
                    }
                }

                Map<Pair<Integer, Integer>, Double> probabilities = new HashMap<>();
                final double singleProbability = 0.25d;

                BiFunction<Integer, Integer, Void> markAsProbable = (Integer x, Integer y) -> {
                    probabilities.compute(new Pair<>(x, y), (key, value) -> {
                        if (value == null)
                            return singleProbability;
                        return value + singleProbability;
                    });
                    return null;
                };

                for (int x = xMin; x <= xMax; ++x) {
                    markAsProbable.apply(x, yMin);
                    markAsProbable.apply(x, yMax);
                }
                for (int y = yMin; y <= yMax; ++y) {
                    markAsProbable.apply(xMin, y);
                    markAsProbable.apply(xMax, y);
                }

                return probabilities.entrySet().stream()
                        .map(in -> new Triplet<>(in.getKey().getValue0(), in.getKey().getValue1(), in.getValue()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
