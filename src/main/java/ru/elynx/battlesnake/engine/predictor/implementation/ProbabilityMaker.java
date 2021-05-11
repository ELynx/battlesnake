package ru.elynx.battlesnake.engine.predictor.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;

public class ProbabilityMaker {
    private static final int MAX_ITEMS = 4;

    private static final int X_STACK_POSITION = 0;
    private static final int Y_STACK_POSITION = 1;
    private static final int SCORE_STACK_POSITION = 2;
    private static final int STACK_SIZE_PER_ITEM = SCORE_STACK_POSITION + 1;

    private int[] stack;
    private int stackPosition;
    private int totalScore = 0;

    public ProbabilityMaker() {
        stack = new int[MAX_ITEMS * STACK_SIZE_PER_ITEM];
        stackPosition = 0;
        totalScore = 0;
    }

    public void reset() {
        stackPosition = 0;
        totalScore = 0;
    }

    public boolean isEmpty() {
        return stackPosition == 0;
    }

    public void add(int x, int y) {
        add(x, y, 1);
    }

    public void add(int x, int y, int score) {
        if (score > 0) {
            stack[stackPosition + X_STACK_POSITION] = x;
            stack[stackPosition + Y_STACK_POSITION] = y;
            stack[stackPosition + SCORE_STACK_POSITION] = score;
            stackPosition += STACK_SIZE_PER_ITEM;
            totalScore += score;
        }
    }

    public List<Triplet<Integer, Integer, Double>> make() {
        if (stackPosition == 0)
            return Collections.emptyList();

        List<Triplet<Integer, Integer, Double>> result = new ArrayList<>(stackPosition / STACK_SIZE_PER_ITEM);

        for (int i = 0; i < stackPosition; i += STACK_SIZE_PER_ITEM) {
            result.add(new Triplet<>(stack[i + X_STACK_POSITION], stack[i + Y_STACK_POSITION],
                    stack[i + SCORE_STACK_POSITION] / (double) totalScore));
        }

        return result;
    }
}
