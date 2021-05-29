package ru.elynx.battlesnake.engine.predictor.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;
import ru.elynx.battlesnake.protocol.CoordsDto;

public class ProbabilityMaker {
    private static final int MAX_ITEMS = 4;

    private static final int X_STACK_POSITION = 0;
    private static final int Y_STACK_POSITION = 1;
    private static final int SCORE_STACK_POSITION = 2;
    private static final int STACK_SIZE_PER_ITEM = SCORE_STACK_POSITION + 1;

    private static final int LEAST_SCORE = 1;

    private final int[] stack;
    private int stackPosition;
    private int totalScore;

    public ProbabilityMaker() {
        stack = new int[MAX_ITEMS * STACK_SIZE_PER_ITEM];

        clearStack();
        clearTotalScore();
    }

    public void reset() {
        clearStack();
        clearTotalScore();
    }

    private void clearStack() {
        stackPosition = 0;
    }

    private void clearTotalScore() {
        totalScore = 0;
    }

    public boolean isEmpty() {
        return stackPosition == 0;
    }

    public void addPosition(CoordsDto coords) {
        addPositionImpl(coords, LEAST_SCORE);
    }

    public void addPositionWithScore(CoordsDto coords, int score) {
        if (score >= LEAST_SCORE) {
            addPositionImpl(coords, score);
        }
    }

    private void addPositionImpl(CoordsDto coords, int score) {
        putPositionOnStack(coords, score);
        increaseTotalScore(score);
    }

    private void putPositionOnStack(CoordsDto coords, int score) {
        stack[stackPosition + X_STACK_POSITION] = coords.getX();
        stack[stackPosition + Y_STACK_POSITION] = coords.getY();
        stack[stackPosition + SCORE_STACK_POSITION] = score;

        stackPosition += STACK_SIZE_PER_ITEM;
    }

    private void increaseTotalScore(int score) {
        totalScore += score;
    }

    public List<Triplet<Integer, Integer, Double>> makeProbabilities() {
        if (isEmpty())
            return Collections.emptyList();

        return makeProbabilitiesImpl();
    }

    private List<Triplet<Integer, Integer, Double>> makeProbabilitiesImpl() {
        List<Triplet<Integer, Integer, Double>> result = new ArrayList<>(stackPosition / STACK_SIZE_PER_ITEM);
        for (int i = 0; i < stackPosition; i += STACK_SIZE_PER_ITEM) {
            result.add(makeProbabilityFromStack(i));
        }

        return result;
    }

    private Triplet<Integer, Integer, Double> makeProbabilityFromStack(int stackOffset) {
        int x = stack[stackOffset + X_STACK_POSITION];
        int y = stack[stackOffset + Y_STACK_POSITION];
        double p = stack[stackOffset + SCORE_STACK_POSITION] / (double) totalScore;

        return new Triplet<>(x, y, p);
    }
}
