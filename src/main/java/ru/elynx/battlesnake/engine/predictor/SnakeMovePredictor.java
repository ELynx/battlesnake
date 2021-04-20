package ru.elynx.battlesnake.engine.predictor;

import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    protected IPredictorInformant informant;

    protected ProbabilityMaker probabilityMaker;

    public SnakeMovePredictor(IPredictorInformant informant) {
        this.informant = informant;

        this.probabilityMaker = new ProbabilityMaker();
    }

    protected void addIfWalkable(int x, int y) {
        if (informant.isWalkable(x, y)) {
            add(x, y);
        }
    }

    protected void add(int x, int y) {
        probabilityMaker.add(x, y);
    }

    public List<Triplet<Integer, Integer, Double>> predict(SnakeDto snake) {
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
            addIfWalkable(x1 - 1, y1);
            addIfWalkable(x1, y1 + 1);
            addIfWalkable(x1 + 1, y1);
            addIfWalkable(x1, y1 + 1);

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

        // TODO more predictions

        addIfWalkable(xf, yf);
        addIfWalkable(xl, yl);
        addIfWalkable(xr, yr);

        return probabilityMaker.make();
    }
}
