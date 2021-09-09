package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.javatuples.Pair;

@UtilityClass
public class CartesianProductMaker {
    public <E> List<Pair<List<E>, Double>> make(List<List<Pair<E, Double>>> allEntities) {
        if (allEntities.isEmpty()) {
            // there is 100% chance no elements are present
            return List.of(new Pair<>(Collections.emptyList(), 1.0d));
        }

        var result = new ArrayList<Pair<List<E>, Double>>();

        // start copy from stack overflow
        // https://stackoverflow.com/a/9591777/15529473
        int solutions = 1;

        for (var singleEntity : allEntities) {
            solutions *= singleEntity.size();
        }

        for (int i = 0; i < solutions; i++) {
            int j = 1;

            var solution = new ArrayList<E>(allEntities.size());
            double probability = 1.0d;

            for (var singleEntity : allEntities) {
                int index = (i / j) % singleEntity.size();
                var singleElement = singleEntity.get(index);

                solution.add(singleElement.getValue0());
                probability *= singleElement.getValue1();

                j *= singleEntity.size();
            }

            result.add(new Pair<>(solution, probability));
        }
        // end copy from stack overflow

        return result;
    }
}
