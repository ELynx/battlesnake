package ru.elynx.battlesnake.engine.predictor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import ru.elynx.battlesnake.engine.predictor.implementation.MoveScoreMaker;
import ru.elynx.battlesnake.engine.predictor.implementation.ProbabilityMaker;
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
        Collection<? extends Coordinates> directions = possibleDirections(snake);
        MoveScoreMaker moveScoreMaker = createScoreMaker(snake, gameState);

        return getProbabilitiesOf(directions, moveScoreMaker);
    }

    private MoveScoreMaker createScoreMaker(Snake snake, GameState gameState) {
        return new MoveScoreMaker(snake, gameState, predictorInformant);
    }

    private Collection<? extends Coordinates> possibleDirections(Snake snake) {
        if (snake.isTimedOut()) {
            return getRepeatLastMove(snake);
        }

        return getWalkableMoves(snake);
    }

    private List<? extends Coordinates> getRepeatLastMove(Snake snake) {
        return List.of(calculateRepeatLastMove(snake));
    }

    private Coordinates calculateRepeatLastMove(Snake snake) {
        Coordinates dXdY = calculateDxDy(snake);

        // default to UP
        if (Coordinates.ZERO.equals(dXdY)) {
            dXdY = Coordinates.ZERO.withY(1);
        }

        Coordinates head = snake.getHead();
        return new Coordinates(head.getX() + dXdY.getX(), head.getY() + dXdY.getY());
    }

    private Coordinates calculateDxDy(Snake snake) {
        // head position this turn
        Coordinates head = snake.getHead();
        // head position last turn
        Coordinates neck = snake.getNeck();

        return new Coordinates(head.getX() - neck.getX(), head.getY() - neck.getY());
    }

    private List<? extends Coordinates> getWalkableMoves(Snake snake) {
        return snake.getAdvancingMoves().stream().filter(predictorInformant::isWalkable).collect(Collectors.toList());
    }

    private List<Pair<Coordinates, Double>> getProbabilitiesOf(Collection<? extends Coordinates> directions,
            MoveScoreMaker moveScoreMaker) {
        List<Pair<Coordinates, Integer>> scoredDirections = new ArrayList<>(directions.size());
        int maxScore = Integer.MIN_VALUE;

        for (Coordinates direction : directions) {
            int score = moveScoreMaker.scoreMove(direction);
            if (score > maxScore) {
                maxScore = score;
            }

            scoredDirections.add(new Pair<>(direction, score));
        }

        return makeProbabilities(scoredDirections, maxScore);
    }

    private List<Pair<Coordinates, Double>> makeProbabilities(List<Pair<Coordinates, Integer>> directions,
            int maxScore) {
        int correction = calculateCorrection(maxScore);
        return makeProbabilitiesWithCorrection(directions, correction);
    }

    private int calculateCorrection(int maxScore) {
        if (maxScore < 1) {
            return 1 - maxScore;
        }

        return 0;
    }

    private List<Pair<Coordinates, Double>> makeProbabilitiesWithCorrection(List<Pair<Coordinates, Integer>> directions,
            int correction) {
        probabilityMaker.reset();

        for (Pair<Coordinates, Integer> direction : directions) {
            probabilityMaker.addPosition(direction.getValue0(), direction.getValue1() + correction);
        }

        return probabilityMaker.makeProbabilities();
    }
}
