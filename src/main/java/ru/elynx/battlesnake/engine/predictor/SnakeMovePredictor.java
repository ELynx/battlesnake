package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    protected IPredictorInformant informant;
    protected ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant informant) {
        this.informant = informant;
        this.probabilityMaker = new ProbabilityMaker();
    }

    protected int score(int x, int y, SnakeDto snake, GameStatePredictor gameState) {
        int score = 1; // start with least positive score

        for (CoordsDto food : gameState.getBoard().getFood()) {
            if (food.getX() == x && food.getY() == y) {
                score += 3; // sum of initial value and food is not enough to jump in front of a train
                break;
            }
        }

        final int ownLength = snake.getLength();
        for (SnakeDto otherSnake : gameState.getBoard().getSnakes()) {
            // manhattan distance 0 of prediction is collision
            if (!snake.getId().equals(otherSnake.getId()) && Util.manhattanDistance(otherSnake.getHead(), x, y) == 1) {
                final int otherLength = otherSnake.getLength();
                if (ownLength < otherLength)
                    score -= 5; // not jump in front of train
                else if (ownLength == otherLength)
                    score += 0; // dicey, no score affect for now
                else // own < other
                    score += 5; // hunt
            }
        }

        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            if (hazard.getX() == x && hazard.getY() == y) {
                score -= 10;
            }
        }

        return score;
    }

    protected void addIfWalkableScored(int x, int y, SnakeDto snake, GameStatePredictor gameState) {
        if (informant.isWalkable(x, y)) {
            probabilityMaker.add(x, y, score(x, y, snake, gameState));
        }
    }

    protected void addIfWalkable(int x, int y) {
        if (informant.isWalkable(x, y)) {
            probabilityMaker.add(x, y);
        }
    }

    protected void add(int x, int y) {
        probabilityMaker.add(x, y);
    }

    public List<Triplet<Integer, Integer, Double>> predict(SnakeDto snake, GameStatePredictor gameState) {
        // graceful error handling
        if (snake.getLength() == 0) {
            return Collections.emptyList();
        }

        probabilityMaker.reset();

        // head this turn
        final int x1 = snake.getHead().getX();
        final int y1 = snake.getHead().getY();

        // there are cases, notably start, when body pieces can overlap
        int x0;
        int y0;

        if (snake.getLength() == 1) {
            x0 = x1;
            y0 = y1;
        } else {
            // head last turn
            x0 = snake.getBody().get(1).getX();
            y0 = snake.getBody().get(1).getY();
        }

        if (x1 == x0 && y1 == y0) {
            // equal possibility to go anywhere
            addIfWalkableScored(x1 - 1, y1, snake, gameState);
            addIfWalkableScored(x1, y1 + 1, snake, gameState);
            addIfWalkableScored(x1 + 1, y1, snake, gameState);
            addIfWalkableScored(x1, y1 + 1, snake, gameState);

            return probabilityMaker.make();
        }

        // delta of this move
        final int dx = x1 - x0;
        final int dy = y1 - y0;

        // forward, repeat last move
        final int xf = x1 + dx;
        final int yf = y1 + dy;

        if (snake.isTimedOut()) {
            // timed out snakes do not care for walk-ability
            add(xf, yf);
            return probabilityMaker.make();
        }

        // magic of matrix multiplication

        // relative turn left
        final int xl = x1 - dy;
        final int yl = y1 + dx;

        // relative turn right
        final int xr = x1 + dy;
        final int yr = y1 - dx;

        addIfWalkableScored(xf, yf, snake, gameState);
        addIfWalkableScored(xl, yl, snake, gameState);
        addIfWalkableScored(xr, yr, snake, gameState);

        // if all choices are negatively bad
        if (probabilityMaker.isEmpty()) {
            // fill in undifferentiated
            addIfWalkable(xf, yf);
            addIfWalkable(xl, yl);
            addIfWalkable(xr, yr);
        }

        return probabilityMaker.make();
    }
}
