package ru.elynx.battlesnake.engine.predictor;

import static ru.elynx.battlesnake.protocol.Move.MovesEnum.*;

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
        if (head.equals(firstBodySegment)) {
            return getFourWayProbability(head);
        }

        // TODO clear up algo

        // delta of this move
        final int dx = x1 - x0;
        final int dy = y1 - y0;

        // forward, repeat last move
        final int xf = x1 + dx;
        final int yf = y1 + dy;

        CoordsDto forward = new CoordsDto(xf, yf);

        if (snake.isTimedOut()) {
            // timed out snakes do not care for walk-ability
            addMove(forward);
            return makeProbabilities();
        }

        // magic of matrix multiplication

        // relative turn left
        final int xl = x1 - dy;
        final int yl = y1 + dx;

        // relative turn right
        final int xr = x1 + dy;
        final int yr = y1 - dx;

        CoordsDto left = new CoordsDto(xl, yl);
        CoordsDto right = new CoordsDto(xr, yr);

        return getProbabilityOfCoords(List.of(forward, left, right));
    }

    private List<Triplet<Integer, Integer, Double>> getFourWayProbability(CoordsDto from) {
        // possibility to go anywhere
        return getProbabilityOfCoords(getFourDirections(from));
    }

    private Iterable<CoordsDto> getFourDirections(CoordsDto from) {
        return List.of(from.plus(UP), from.plus(RIGHT), from.plus(DOWN), from.plus(LEFT));
    }

    // TODO naming
    private List<Triplet<Integer, Integer, Double>> getProbabilityOfCoords(Iterable<CoordsDto> renameCoords) {
        for (CoordsDto coords : renameCoords) {
            addScoredMoveIfWalkable(coords);
        }

        if (noMovesAdded()) {
            for (CoordsDto coords : renameCoords) {
                addMoveIfWalkable(coords);
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

    private void addScoredMoveIfWalkable(CoordsDto coords) {
        if (informant.isWalkable(coords)) {
            addScoredMove(coords);
        }
    }

    private void addScoredMove(CoordsDto coords) {
        int score = scoreMaker.scoreMove(coords);
        addMove(coords, score);
    }

    private void addMove(CoordsDto coords, int score) {
        probabilityMaker.addPositionWithScore(coords, score);
    }

    private void addMoveIfWalkable(CoordsDto coords) {
        if (informant.isWalkable(coords)) {
            addMove(coords);
        }
    }

    private void addMove(CoordsDto coords) {
        probabilityMaker.addPosition(coords);
    }

    private List<Triplet<Integer, Integer, Double>> makeProbabilities() {
        scoreMaker.freeReferences();
        return probabilityMaker.makeProbabilities();
    }
}
