package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Pair;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

public class SnakeMovePredictor {
    private final IPredictorInformant predictorInformant;
    private final ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant predictorInformant) {
        this.predictorInformant = predictorInformant;
        this.probabilityMaker = new ProbabilityMaker();
    }

    public List<Pair<Coordinates, Double>> predict(Snake snake, GameState gameState) {
        if (snake.getLength() == 0) {
            return Collections.emptyList();
        }

        return predictImpl(snake, gameState);
    }

    private List<Pair<Coordinates, Double>> predictImpl(Snake snake, GameState gameState) {
        Iterable<Coordinates> directions = possibleDirections(snake);
        ScoreMaker scoreMaker = new ScoreMaker(snake, gameState);

        return getProbabilityOf(directions, scoreMaker);
    }

    private Iterable<Coordinates> possibleDirections(Snake snake) {
        // head position this turn
        Coordinates head = snake.getHead();
        // head position last turn
        Coordinates firstBodySegment = getFirstBodySegment(snake);

        // there are cases, notably start, when body pieces can overlap
        if (head.equals(firstBodySegment)) {
            // from initial state, any direction is possible
            return head.sideNeighbours();
        }

        int x1 = head.getX();
        int y1 = head.getY();

        int x0 = firstBodySegment.getX();
        int y0 = firstBodySegment.getY();

        // delta of this move
        int dx = x1 - x0;
        int dy = y1 - y0;

        // forward, repeat last move
        int xf = x1 + dx;
        int yf = y1 + dy;

        Coordinates forward = new Coordinates(xf, yf);

        // timed out snake will repeat it's move
        if (snake.isTimedOut()) {
            return List.of(forward);
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

        return List.of(forward, left, right);
    }

    private Coordinates getFirstBodySegment(Snake snake) {
        if (snake.getLength().equals(1)) {
            return snake.getHead();
        } else {
            return snake.getBody().get(1);
        }
    }

    private List<Pair<Coordinates, Double>> getProbabilityOf(Iterable<Coordinates> directions, ScoreMaker scoreMaker) {
        resetMoves();

        for (Coordinates direction : directions) {
            addScoredMoveIfWalkable(direction, scoreMaker);
        }

        if (noMovesAdded()) {
            for (Coordinates direction : directions) {
                addMoveIfWalkable(direction);
            }
        }

        return makeProbabilities();
    }

    private void resetMoves() {
        probabilityMaker.reset();
    }

    private boolean noMovesAdded() {
        return probabilityMaker.isEmpty();
    }

    private void addScoredMoveIfWalkable(Coordinates coordinates, ScoreMaker scoreMaker) {
        if (predictorInformant.isWalkable(coordinates)) {
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
        if (predictorInformant.isWalkable(coordinates)) {
            addMove(coordinates);
        }
    }

    private void addMove(Coordinates coordinates) {
        probabilityMaker.addPosition(coordinates);
    }

    private List<Pair<Coordinates, Double>> makeProbabilities() {
        return probabilityMaker.makeProbabilities();
    }
}
