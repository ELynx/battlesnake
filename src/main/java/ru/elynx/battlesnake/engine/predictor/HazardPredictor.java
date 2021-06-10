package ru.elynx.battlesnake.engine.predictor;

import java.util.*;
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
        if (isRoyale() && isPreHazardExpansionTurn() && hasCellsFreeOfHazard()) {
            return predictHazards();
        }

        return Collections.emptyList();
    }

    private boolean isRoyale() {
        return gameState.getRules().isRoyale();
    }

    private boolean hasCellsFreeOfHazard() {
        return gameState.getBoard().getHazards().size() < gameState.getBoard().getDimensions().area();
    }

    private boolean isPreHazardExpansionTurn() {
        if (hazardStep <= 0) {
            return false;
        }

        return gameState.getTurn() % hazardStep == hazardStep - 1;
    }

    private List<Pair<Coordinates, Double>> predictHazards() {
        FlagMatrix hazardField = prepareHazardField();
        Dimensions dimensions = hazardField.getDimensions();
        int width = dimensions.getWidth();
        int height = dimensions.getHeight();

        int xMin = -1;
        int yMin = -1;
        searchMin : for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (!hazardField.isSet(x, y)) {
                    xMin = x;
                    yMin = y;
                    break searchMin;
                }
            }
        }

        int xMax = -1;
        int yMax = -1;
        searchMax : for (int y = height - 1; y >= 0; --y) {
            for (int x = width - 1; x >= 0; --x) {
                if (!hazardField.isSet(x, y)) {
                    xMax = x;
                    yMax = y;
                    break searchMax;
                }
            }
        }

        return getProbabilities(xMin, xMax, yMin, yMax);
    }

    private FlagMatrix prepareHazardField() {
        FlagMatrix hazardField = FlagMatrix.unsetMatrix(gameState.getBoard().getDimensions());

        for (Coordinates hazard : gameState.getBoard().getHazards()) {
            hazardField.set(hazard);
        }

        return hazardField;
    }

    private List<Pair<Coordinates, Double>> getProbabilities(int xMin, int xMax, int yMin, int yMax) {
        // test for edge case with simple detection
        if (xMin == xMax && yMin == yMax) {
            return List.of(new Pair<>(new Coordinates(xMin, yMin), 1.0d));
        }

        double singleProbability = 0.25d;

        List<Pair<Coordinates, Double>> result = new ArrayList<>();
        for (int x = xMin; x <= xMax; ++x) {
            result.add(new Pair<>(new Coordinates(x, yMin), singleProbability));
            result.add(new Pair<>(new Coordinates(x, yMax), singleProbability));
        }

        for (int y = yMin; y <= yMax; ++y) {
            result.add(new Pair<>(new Coordinates(xMin, y), singleProbability));
            result.add(new Pair<>(new Coordinates(xMax, y), singleProbability));
        }

        // account for corners
        Map<Coordinates, Double> foo = result.stream()
                .collect(Collectors.groupingBy(Pair::getValue0, Collectors.summingDouble(Pair::getValue1)));

        return foo.entrySet().stream().map(x -> new Pair<>(x.getKey(), x.getValue())).collect(Collectors.toList());
    }
}
