package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    private final IPredictorInformant informant;
    private final ScoreMaker scoreMaker;
    private final ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant informant) {
        this.informant = informant;
        this.scoreMaker = new ScoreMaker();
        this.probabilityMaker = new ProbabilityMaker();
    }

    public List<Triplet<Integer, Integer, Double>> predict(SnakeDto snake, GameStatePredictor gameState) {
        // graceful error handling
        if (snake.getLength() == 0) {
            return Collections.emptyList();
        }

        prepareInternals(snake, gameState);

        // head position this turn
        CoordsDto head = snake.getHead();
        int x1 = head.getX();
        int y1 = head.getY();

        // head position last turn
        CoordsDto firstBodySegment = getFirstBodySegment(snake);
        int x0 = firstBodySegment.getX();
        int y0 = firstBodySegment.getY();

        // there are cases, notably start, when body pieces can overlap
        if (x1 == x0 && y1 == y0) {
            return getFourWayProbability(x1, y1);
        }

        // delta of this move
        final int dx = x1 - x0;
        final int dy = y1 - y0;

        // forward, repeat last move
        final int xf = x1 + dx;
        final int yf = y1 + dy;

        if (snake.isTimedOut()) {
            // timed out snakes do not care for walk-ability
            addMove(xf, yf);
            return makeProbabilities();
        }

        // magic of matrix multiplication

        // relative turn left
        final int xl = x1 - dy;
        final int yl = y1 + dx;

        // relative turn right
        final int xr = x1 + dy;
        final int yr = y1 - dx;

        // TODO generator for this
        return getProbabilityOfXYArray(new int[]{xf, yf, xl, yl, xr, yr});
    }

    private List<Triplet<Integer, Integer, Double>> getFourWayProbability(int x, int y) {
        // possibility to go anywhere
        return getProbabilityOfXYArray(getFourDirections(x, y));
    }

    // TODO better type
    private int[] getFourDirections(int x, int y) {
        return new int[]{x - 1, y, x, y + 1, x + 1, y, x, y + 1};
    }

    private List<Triplet<Integer, Integer, Double>> getProbabilityOfXYArray(int[] items) {
        for (int i = 0; i < items.length; i = i + 2) {
            int x = items[i];
            int y = items[i + 1];
            addScoredMoveIfWalkable(x, y);
        }

        if (noMovesAdded()) {
            for (int i = 0; i < items.length; i = i + 2) {
                int x = items[i];
                int y = items[i + 1];
                addMoveIfWalkable(x, y);
            }
        }

        return makeProbabilities();
    }

    private void prepareInternals(SnakeDto snake, GameStatePredictor gameState) {
        scoreMaker.reset(snake, gameState);
        probabilityMaker.reset();
    }

    private CoordsDto getFirstBodySegment(SnakeDto snake) {
        if (snake.getLength().equals(1)) {
            return snake.getHead();
        } else {
            return snake.getBody().get(1);
        }
    }

    private boolean noMovesAdded() {
        return probabilityMaker.isEmpty();
    }

    private void addScoredMoveIfWalkable(int x, int y) {
        if (informant.isWalkable(x, y)) {
            addScoredMove(x, y);
        }
    }

    private void addScoredMove(int x, int y) {
        int score = scoreMaker.scoreMove(x, y);
        addMove(x, y, score);
    }

    private void addMove(int x, int y, int score) {
        probabilityMaker.addPositionWithScore(x, y, score);
    }

    private void addMoveIfWalkable(int x, int y) {
        if (informant.isWalkable(x, y)) {
            addMove(x, y);
        }
    }

    private void addMove(int x, int y) {
        probabilityMaker.addPosition(x, y);
    }

    private List<Triplet<Integer, Integer, Double>> makeProbabilities() {
        scoreMaker.freeReferences();
        return probabilityMaker.makeProbabilities();
    }
}
