package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class GameStatePredictor extends GameStateDto {
    private static final int INITIAL_LENGTH = 3;
    private static final int MAX_HEALTH = 100;
    private static final String ROYALE_RULESET_NAME = "royale";

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
        if (hazardStep != 0 && ROYALE_RULESET_NAME.equalsIgnoreCase(getGame().getRuleset().getName())) {
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
                FreeSpaceMatrix freeSpaceMatrix = FreeSpaceMatrix.emptyFreeSpaceMatrix(width, height);

                for (CoordsDto hazard : getBoard().getHazards()) {
                    freeSpaceMatrix.setOccupied(hazard.getX(), hazard.getY());
                }

                int xMin = -1;
                int xMax = -1;
                int yMin = -1;
                int yMax = -1;

                for (int x = 0; x < width; ++x) {
                    if (yMin == -1 && yMax == -1) {
                        int y = 0;
                        for (y = 0; y < height; ++y)
                            if (freeSpaceMatrix.getSpace(x, y) > 0) {
                                yMin = y;
                                break;
                            }

                        for (; y < height; ++y)
                            if (freeSpaceMatrix.getSpace(x, y) > 0)
                                yMax = y;
                            else
                                break;
                    }

                    if (yMin != -1) {
                        if (xMin == -1)
                            xMin = x;

                        if (freeSpaceMatrix.getSpace(x, yMin) > 0)
                            xMax = x;
                        else
                            break;
                    }
                }

                List<Triplet<Integer, Integer, Double>> result = new LinkedList<>();

                // TODO output only unique locations

                // corners are intentionally twice more dangerous
                double probability = 0.25d;
                for (int x = xMin; x <= xMax; ++x) {
                    result.add(new Triplet<>(x, yMin, probability));
                    result.add(new Triplet<>(x, yMax, probability));
                }
                for (int y = yMin; y <= yMax; ++y) {
                    result.add(new Triplet<>(xMin, y, probability));
                    result.add(new Triplet<>(xMax, y, probability));
                }

                return result;
            }
        }

        return Collections.emptyList();
    }
}
