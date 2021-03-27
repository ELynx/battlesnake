package ru.elynx.battlesnake.engine.strategies.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javatuples.KeyValue;
import ru.elynx.battlesnake.protocol.CoordsDto;

class FlatProbabilityMaker {
    protected int[] stack;
    protected int stackPos;

    FlatProbabilityMaker() {
        stack = new int[8];
    }

    void reset() {
        stackPos = 0;
    }

    void add(int x, int y) {
        stack[stackPos] = x;
        stack[stackPos + 1] = y;
        stackPos += 2;
    }

    List<KeyValue<CoordsDto, Double>> make() {
        if (stackPos == 0)
            return Collections.emptyList();

        List<KeyValue<CoordsDto, Double>> result = new ArrayList<>(stackPos / 2);

        // 1 / (size / 2) -> 2 / size
        double p = 2.0 / (double) stackPos;
        for (int i = 0; i < stackPos; i += 2) {
            result.add(new KeyValue<>(new CoordsDto(stack[i], stack[i + 1]), p));
        }

        return result;
    }
}
