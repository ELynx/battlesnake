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

    protected int hazardStep = 0;

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
        if (hazardStep != 0 && "royale".equalsIgnoreCase(getGame().getRuleset().getName())) {
            // if everything is hazard, there is no more prediction
            if (getBoard().getHazards().size() >= getBoard().getWidth() * getBoard().getHeight()) {
                return Collections.emptyList();
            }

            // Go has deterministic random, and only two lower bytes are used
            // seed is re-used through game, and business logic is based on repeatability
            // in theory, it is quite possible to determine hazards accurately

            if (getTurn() % hazardStep == hazardStep - 1) {
                FreeSpaceMatrix freeSpaceMatrix = FreeSpaceMatrix.emptyFreeSpaceMatrix(getBoard().getWidth(),
                        getBoard().getHeight());

                for (CoordsDto hazard : getBoard().getHazards()) {
                    freeSpaceMatrix.setOccupied(hazard.getX(), hazard.getY());
                }

                int xMin = -1;
                int xMax = -1;
                int yMin = -1;
                int yMax = -1;

                for (int x = 0; x < getBoard().getWidth(); ++x) {
                    if (yMin == -1 && yMax == -1) {
                        int y = 0;
                        for (y = 0; y < getBoard().getHeight() && yMin == -1; ++y)
                            if (freeSpaceMatrix.getSpace(x, y) > 0)
                                yMin = y;

                        for (; y < getBoard().getHeight(); ++y)
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
