package ru.elynx.battlesnake.engine.predictor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.Getter;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.GameState;

public class HazardPredictor {
    private final GameState gameState;
    private final int hazardStep;

    @Getter(lazy = true)
    private final List<Triplet<Integer, Integer, Double>> predictedHazards = getPredictedHazardsImpl();

    public HazardPredictor(GameState gameState, int hazardStep) {
        this.gameState = gameState;
        this.hazardStep = hazardStep;
    }

    // TODO this is a plug
    public GameState getGameState() {
        return gameState;
    }

    private List<Triplet<Integer, Integer, Double>> getPredictedHazardsImpl() {
        if (hazardStep != 0 && gameState.getRules().isRoyale()) {
            Dimensions dimensions = gameState.getBoard().getDimensions();

            // if everything is hazard, there is no more prediction
            if (gameState.getBoard().getHazards().size() >= dimensions.area()) {
                return Collections.emptyList();
            }

            // Go has very deterministic pseudorandom, and only two lower bits are used
            // seed is re-used through game, and business logic is based on repeatability
            // in theory, it is quite possible to determine hazards accurately

            if (gameState.getTurn() % hazardStep == hazardStep - 1) {
                FreeSpaceMatrix freeSpaceMatrix = FreeSpaceMatrix.emptyMatrix(dimensions);

                for (Coordinates hazard : gameState.getBoard().getHazards()) {
                    freeSpaceMatrix.setOccupied(hazard);
                }

                int xMin = -1;
                int xMax = -1;
                int yMin = -1;
                int yMax = -1;

                for (int x = 0; x < dimensions.getWidth(); ++x) {
                    if (yMin == -1 && yMax == -1) {
                        int y;
                        for (y = 0; y < dimensions.getHeight(); ++y)
                            // TODO type
                            if (freeSpaceMatrix.isFree(new Coordinates(x, y))) {
                                yMin = y;
                                break;
                            }

                        for (; y < dimensions.getHeight(); ++y)
                            // TODO type
                            if (freeSpaceMatrix.isFree(new Coordinates(x, y)))
                                yMax = y;
                            else
                                break;
                    }

                    if (yMin != -1) {
                        if (xMin == -1)
                            xMin = x;

                        // TODO type
                        if (freeSpaceMatrix.isFree(new Coordinates(x, yMin)))
                            xMax = x;
                        else
                            break;
                    }
                }

                Map<Pair<Integer, Integer>, Double> probabilities = new HashMap<>();
                double singleProbability = 0.25d;

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
