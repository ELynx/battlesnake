package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.javatuples.Pair;

@UtilityClass
public class CartesianProduct {
    /**
     * Make cartesian product with probabilities.<br>
     * <br>
     * Sample explanation: two people play Rock-Paper-Scissors.<br>
     * Event A: Player Alice plays Rock with probability of 0.5 and Scissors with
     * probability of 0.5.<br>
     * Event B: Player Bob plays Rock 0.25, Paper 0.25 and Scissors 0.5.<br>
     * They play independently (probability multiplication rule apply).<br>
     * Cartesian product of their plays is all possible combinations of their moves
     * with probabilities:<br>
     * Alice plays Rock, Bob plays Rock -> 0.5 x 0.25 -> 0.125<br>
     * Alice plays Rock, Bob plays Paper -> 0.125<br>
     * Alice plays Rock, Bob plays Scissors -> 0.25<br>
     * Alice plays Scissors, Bob plays Rock -> 0.125<br>
     * Alice plays Scissors, Bob plays Paper -> 0.125<br>
     * Alice plays Scissors, Bob plays Scissors -> 0.25<br>
     *
     * @param allEntities
     *            All individual entities.<br>
     *            Structure from inner to outer:<br>
     *            Pair<E, Double> - Outcome with probability within event.<br>
     *            {Alice, Rock}, 0.5<br>
     *            List<...> - All outcomes of event. Usually probabilities add up to
     *            1.0.<br>
     *            [{ {Alice, Rock}, 0.5 }, { {Alice, Scissors}, 0.5 }]<br>
     *            List<...> - All independent events.<br>
     *            [ [Alice's plays], [Bob's plays] ]
     * @param <E>
     *            Outcome. Since ordering is not guaranteed, should be
     *            self-meaningful. In Rock-Paper-Scissors example, this could be<br>
     *            { player name, player move }.
     * @return All combinations of outcomes from each event, with calculated
     *         probabilities.<br>
     *         Structure from inner to outer:<br>
     *         List<E> - particular set of outcomes.<br>
     *         [ {Alice, Rock}, {Bob, Scissors} ]<br>
     *         Pair<..., Double> - set of outcomes and it's probability.<br>
     *         { [ {Alice, Rock}, {Bob, Scissors} ], 0.25 }<br>
     *         List<...> - all outcomes and their probabilities.<br>
     *         <b>No ordering of elements in any of lists is guaranteed.</b> <br>
     *         Special case 1: outer list is empty, e.g. no one showed up to the
     *         game.<br>
     *         Returned List of single outcome with no elements.<br>
     *         [ {[], 1.0} ]<br>
     *         Special case 2: one or more of inner lists is empty, e.g. one of
     *         players did not show up to the game.<br>
     *         Returned as special case 1.
     */
    public <E> List<Pair<List<E>, Double>> make(List<List<Pair<E, Double>>> allEntities) {
        if (allEntities.isEmpty()) {
            return makeZero();
        }

        var result = new ArrayList<Pair<List<E>, Double>>();

        // start copy from stack overflow
        // https://stackoverflow.com/a/9591777/15529473
        int solutions = 1;

        for (var singleEntity : allEntities) {
            solutions *= singleEntity.size();
        }

        // TODO work out on examples
        // if (solutions == 0) {
        // return makeZero();
        // }

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

    private <E> List<Pair<List<E>, Double>> makeZero() {
        // there is 100% chance no elements are present
        return List.of(new Pair<>(Collections.emptyList(), 1.0d));
    }
}
