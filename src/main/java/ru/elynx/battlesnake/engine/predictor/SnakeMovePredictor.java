package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Snake;

public class SnakeMovePredictor {
    private final IPredictorInformant informant;
    private final ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant informant) {
        this.informant = informant;
        this.probabilityMaker = new ProbabilityMaker();
    }

    public List<Triplet<Integer, Integer, Double>> predict(Snake snake, HazardPredictor hazardPredictor) {
        // graceful error handling
        if (snake.getLength() == 0) {
            return Collections.emptyList();
        }

        prepareInternals();

        ScoreMaker scoreMaker = new ScoreMaker(snake, hazardPredictor.getGameState());

        // head position this turn
        Coordinates head = snake.getHead();
        int x1 = head.getX();
        int y1 = head.getY();

        // head position last turn
        Coordinates firstBodySegment = getFirstBodySegment(snake);
        int x0 = firstBodySegment.getX();
        int y0 = firstBodySegment.getY();

        // there are cases, notably start, when body pieces can overlap
        if (head.equals(firstBodySegment)) {
            return getFourWayProbability(head, scoreMaker);
        }

        // TODO clear up algo

        // delta of this move
        int dx = x1 - x0;
        int dy = y1 - y0;

        // forward, repeat last move
        int xf = x1 + dx;
        int yf = y1 + dy;

        Coordinates forward = new Coordinates(xf, yf);

        if (snake.isTimedOut()) {
            // timed out snakes do not care for walk-ability
            addMove(forward);
            return makeProbabilities();
        }

        // magic of matrix multiplication

        // relative turn left
        int xl = x1 - dy;
        int yl = y1 + dx;

        // relative turn right
        int xr = x1 + dy;
        int yr = y1 - dx;

        Coordinates left = new Coordinates(xl, yl);
        Coordinates right = new Coordinates(xr, yr);

        return getProbabilityOfCoords(List.of(forward, left, right), scoreMaker);
    }

    private List<Triplet<Integer, Integer, Double>> getFourWayProbability(Coordinates from, ScoreMaker scoreMaker) {
        // possibility to go anywhere
        return getProbabilityOfCoords(from.sideNeighbours(), scoreMaker);
    }

    // TODO naming
    private List<Triplet<Integer, Integer, Double>> getProbabilityOfCoords(Iterable<Coordinates> coordinates,
            ScoreMaker scoreMaker) {
        // TODO rename
        for (Coordinates cell : coordinates) {
            addScoredMoveIfWalkable(cell, scoreMaker);
        }

        if (noMovesAdded()) {
            for (Coordinates cell : coordinates) {
                addMoveIfWalkable(cell);
            }
        }

        return makeProbabilities();
    }

    private void prepareInternals() {
        probabilityMaker.reset();
    }

    private Coordinates getFirstBodySegment(Snake snake) {
        if (snake.getLength().equals(1)) {
            return snake.getHead();
        } else {
            return snake.getBody().get(1);
        }
    }

    private boolean noMovesAdded() {
        return probabilityMaker.isEmpty();
    }

    private void addScoredMoveIfWalkable(Coordinates coordinates, ScoreMaker scoreMaker) {
        if (informant.isWalkable(coordinates)) {
            addScoredMove(coordinates, scoreMaker);
        }
    }

    private void addScoredMove(Coordinates coordinates, ScoreMaker scoreMaker) {
        int score = scoreMaker.scoreMove(coordinates);
        addMove(coordinates, score);
    }

    private void addMove(Coordinates coordinates, int score) {
        probabilityMaker.addPositionWithScore(coordinates, score);
    }

    private void addMoveIfWalkable(Coordinates coordinates) {
        if (informant.isWalkable(coordinates)) {
            addMove(coordinates);
        }
    }

    private void addMove(Coordinates coordinates) {
        probabilityMaker.addPosition(coordinates);
    }

    private List<Triplet<Integer, Integer, Double>> makeProbabilities() {
        return probabilityMaker.makeProbabilities();
    }
}
