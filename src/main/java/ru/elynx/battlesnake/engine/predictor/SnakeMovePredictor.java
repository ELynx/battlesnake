package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    protected IPredictorInformant informant;
    protected ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant informant) {
        this.informant = informant;
        this.probabilityMaker = new ProbabilityMaker();
    }

    protected void addScoredMoveIfWalkable(int x, int y, ScoreMaker scoreMaker) {
        if (informant.isWalkable(x, y)) {
            int score = scoreMaker.scoreMove(x, y);
            probabilityMaker.add(x, y, score);
        }
    }

    protected void addMoveIfWalkable(int x, int y) {
        if (informant.isWalkable(x, y)) {
            probabilityMaker.add(x, y);
        }
    }

    protected void addMove(int x, int y) {
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

        ScoreMaker scoreMaker = new ScoreMaker(snake, gameState);

        if (x1 == x0 && y1 == y0) {
            // equal possibility to go anywhere
            addScoredMoveIfWalkable(x1 - 1, y1, scoreMaker);
            addScoredMoveIfWalkable(x1, y1 + 1, scoreMaker);
            addScoredMoveIfWalkable(x1 + 1, y1, scoreMaker);
            addScoredMoveIfWalkable(x1, y1 + 1, scoreMaker);

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
            addMove(xf, yf);
            return probabilityMaker.make();
        }

        // magic of matrix multiplication

        // relative turn left
        final int xl = x1 - dy;
        final int yl = y1 + dx;

        // relative turn right
        final int xr = x1 + dy;
        final int yr = y1 - dx;

        addScoredMoveIfWalkable(xf, yf, scoreMaker);
        addScoredMoveIfWalkable(xl, yl, scoreMaker);
        addScoredMoveIfWalkable(xr, yr, scoreMaker);

        // if all choices are negatively bad
        if (probabilityMaker.isEmpty()) {
            // fill in undifferentiated
            addMoveIfWalkable(xf, yf);
            addMoveIfWalkable(xl, yl);
            addMoveIfWalkable(xr, yr);
        }

        return probabilityMaker.make();
    }
}
