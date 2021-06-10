package ru.elynx.battlesnake.engine.predictor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.javatuples.Pair;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.GameState;

public class HazardPredictor {
    private final GameState gameState;
    private final int hazardStep;

    @Getter(lazy = true)
    private final List<Pair<Coordinates, Double>> predictedHazards = getPredictedHazardsImpl();

    // TODO make GameState not subordinate of HazardPredictor
    public HazardPredictor(GameState gameState, int hazardStep) {
        this.gameState = gameState;
        this.hazardStep = hazardStep;
    }

    public GameState getGameState() {
        return gameState;
    }

    // Go has very deterministic pseudorandom, and only two lower bits are used
    // seed is re-used through game, and business logic is based on repeatability
    // in theory, it is quite possible to determine hazards accurately
    private List<Pair<Coordinates, Double>> getPredictedHazardsImpl() {
        if (isRoyale() && isPreHazardTurn() && hasNonHazardCells()) {
            return predictHazards();
        }

        return Collections.emptyList();
    }

    private boolean isRoyale() {
        return hazardStep > 0 && gameState.getRules().isRoyale();
    }

    private boolean hasNonHazardCells() {
        return gameState.getBoard().getHazards().size() < gameState.getBoard().getDimensions().area();
    }

    private boolean isPreHazardTurn() {
        return gameState.getTurn() % hazardStep == hazardStep - 1;
    }

    private List<Pair<Coordinates, Double>> predictHazards() {
        Dimensions dimensions = gameState.getBoard().getDimensions();
        FlagMatrix hazardField = FlagMatrix.unsetMatrix(dimensions);

        for (Coordinates hazard : gameState.getBoard().getHazards()) {
            hazardField.set(hazard);
        }

        int xMin = -1;
        int xMax = -1;
        int yMin = -1;
        int yMax = -1;

        for (int x = 0; x < dimensions.getWidth(); ++x) {
            if (yMin == -1 && yMax == -1) {
                int y;
                for (y = 0; y < dimensions.getHeight(); ++y)
                    if (!hazardField.isSet(x, y)) {
                        yMin = y;
                        break;
                    }

                for (; y < dimensions.getHeight(); ++y)
                    if (!hazardField.isSet(x, y))
                        yMax = y;
                    else
                        break;
            }

            if (yMin != -1) {
                if (xMin == -1)
                    xMin = x;

                if (!hazardField.isSet(x, yMin))
                    xMax = x;
                else
                    break;
            }
        }

        Map<Coordinates, Double> probabilities = new HashMap<>();
        double singleProbability = 0.25d;

        Function<Coordinates, Void> markAsProbable = (Coordinates coordinates) -> {
            probabilities.compute(coordinates, (key, value) -> {
                if (value == null)
                    return singleProbability;
                return value + singleProbability;
            });
            return null;
        };

        for (int x = xMin; x <= xMax; ++x) {
            // TODO type
            markAsProbable.apply(new Coordinates(x, yMin));
            // TODO type
            markAsProbable.apply(new Coordinates(x, yMax));
        }

        for (int y = yMin; y <= yMax; ++y) {
            // TODO type
            markAsProbable.apply(new Coordinates(xMin, y));
            // TODO type
            markAsProbable.apply(new Coordinates(xMax, y));
        }

        return probabilities.entrySet().stream().map(in -> new Pair<>(in.getKey(), in.getValue()))
                .collect(Collectors.toList());
    }
}
