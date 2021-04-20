package ru.elynx.battlesnake.engine.predictor.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;

public class ProbabilityMaker {
    private static final int MAX_ITEMS = 4;

    private static final int _X = 0;
    private static final int _Y = 1;
    private static final int _S = 2;
    private static final int STACK_PER_ITEM = _S + 1;

    private int[] stack;
    private int stackPos;
    private int totalScore = 0;

    public ProbabilityMaker() {
        stack = new int[MAX_ITEMS * STACK_PER_ITEM];
        stackPos = 0;
        totalScore = 0;
    }

    public void reset() {
        stackPos = 0;
        totalScore = 0;
    }

    public void add(int x, int y) {
        add(x, y, 1);
    }

    public void add(int x, int y, int score) {
        if (score > 0) {
            stack[stackPos + _X] = x;
            stack[stackPos + _Y] = y;
            stack[stackPos + _S] = score;
            stackPos += STACK_PER_ITEM;
            totalScore += score;
        }
    }

    public List<Triplet<Integer, Integer, Double>> make() {
        if (stackPos == 0)
            return Collections.emptyList();

        List<Triplet<Integer, Integer, Double>> result = new ArrayList<>(stackPos / STACK_PER_ITEM);

        for (int i = 0; i < stackPos; i += STACK_PER_ITEM) {
            result.add(new Triplet<>(stack[i + _X], stack[i + _Y], stack[i + _S] / (double) totalScore));
        }

        return result;
    }
}
