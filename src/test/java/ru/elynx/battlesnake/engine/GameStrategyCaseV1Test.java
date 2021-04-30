package ru.elynx.battlesnake.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.elynx.battlesnake.engine.GameStrategyBasicTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.protocol.Move.Moves.*;
import static ru.elynx.battlesnake.protocol.RulesetDto.ROYALE_RULESET_NAME;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.testspecific.TestMove;
import ru.elynx.battlesnake.testspecific.ToApiVersion;

@SpringBootTest
class GameStrategyCaseV1Test {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    // ported from V0 to V1 for ease of understanding
    // Y snake extended downwards to prevent "move-out-opening"
    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_empty_space_better_than_snake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "___A_______\n" + //
                "___aaa_yyy_\n" + //
                "_____BbY_y_\n" + //
                "______b_yy_\n" + //
                "____bbb_y__\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(DOWN));
    }

    // ported from V0 to V1 for ease of understanding
    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_surrounded_by_snake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "yyyv<______\n" + //
                "y0^<^______\n" + //
                "yY__y______\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n").setHealth("Y", 2);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), not(equalToIgnoringCase(UP)));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_easy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        // easy because there is no way out if entering fruit corner
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_________yY\n" + //
                "____yyyyyy0\n").setHealth("Y", 2);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(UP));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_hard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        // hard because it is necessary to predict that growth would close the exit
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_________yY\n" + //
                "_________y0\n").setHealth("Y", 2);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(UP));
    }

    // health is left at max to avoid starvation rage
    // more of way to prevent greedy grab from under the train
    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        // head to head even with snake of same length is lose
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y\n" + //
                "____y\n" + //
                "____Y\n" + //
                "____0\n" + //
                "____A\n" + //
                "____a\n" + //
                "____a\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), not(equalToIgnoringCase(DOWN)));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food_and_hunt(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y____\n" + //
                "____y____\n" + //
                "____Y____\n" + //
                "__bB0Cc__\n" + //
                "____A____\n" + //
                "____a____\n" + //
                "____a____\n").setHealth("Y", 2);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), not(equalToIgnoringCase(DOWN)));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_novice(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        String[] circles = {"Y_\n__", "_Y\n__", "__\nY_", "__\n_Y"};
        String[] notTo = {LEFT, UP, UP, RIGHT, DOWN, LEFT, RIGHT, DOWN};
        assertThat(notTo.length, is(circles.length * 2));

        for (int i = 0; i < circles.length; ++i) {
            int j = i * 2;
            int k = j + 1;

            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
            assertThat("Step " + i, move.getMove(), not(equalToIgnoringCase(notTo[j])));
            assertThat("Step " + i, move.getMove(), not(equalToIgnoringCase(notTo[k])));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_easy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        String[] circles = {"Yy\n__", "_Y\n_y", "__\nyY", "y_\nY_", "Y_\ny_", "yY\n__", "_y\n_Y", "__\nYy"};
        String[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
            assertThat("Step " + i, move.getMove(), equalToIgnoringCase(to[i]));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_medium(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        String[] circles = {"Yy\n_y", "_Y\nyy", "y_\nyY", "yy\nY_", "Y_\nyy", "yY\ny_", "yy\n_Y", "_y\nYy"};
        String[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
            assertThat("Step " + i, move.getMove(), equalToIgnoringCase(to[i]));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_hard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        String[] circles = {"Y<\n>^", "vY\n>^", "v<\n>Y", "v<\nY^", "Yv\n^<", ">Y\n^<", ">v\n^Y", ">v\nY<"};
        String[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
            assertThat("Step " + i, move.getMove(), equalToIgnoringCase(to[i]));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_give_up(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        // given no food spawns, tail will clear out the passage out in 5 turns
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____Y\n" + //
                ">>>>^\n" + //
                "^<<<_\n" + //
                "_____\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(LEFT));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_eat_in_hazard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "_____0_____\n" + //
                "___A_____0_\n" + //
                "__v^______0\n" + //
                "_v<^_______\n" + //
                "_>>^_bB____\n" + //
                "__bbv^_>v0_\n" + //
                "_bb_>^_^v0Y\n" + //
                "_b_____^v_y\n" + //
                "_______^v_y\n" + //
                "_____0__y_y\n" + //
                "__0_____yyy\n");

        generator.setRulesetName(ROYALE_RULESET_NAME);
        generator.setTurn(122);
        generator.setHealth("A", 64);
        generator.setHealth("B", 52);
        generator.setHealth("Y", 69);
        generator.setLatency("A", 38);
        generator.setLatency("B", 16);
        generator.setLatency("Y", 77);
        generator.setHazards("" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(LEFT));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_the_inevitable(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "_________c_\n" + //
                "_________c_\n" + //
                "_________vC\n" + //
                "_________>^\n" + //
                "____a_aaaaA\n" + //
                "0___aaayyY_\n" + //
                "____yyyy___\n" + //
                "____y______\n" + //
                "___yy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 99);
        generator.setHealth("C", 86);
        generator.setHealth("Y", 95);
        generator.setLatency("A", 81);
        generator.setLatency("C", 58);
        generator.setLatency("Y", 66);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(RIGHT));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_does_not_go_into_hazard_lake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "y________W<\n" + //
                "yy__wwwww>^\n" + //
                "_y_________\n" + //
                "yy_________\n" + //
                "y__________\n" + //
                "Y__________\n" + //
                "_v<<<______\n" + //
                "_>>>>xX____\n" + //
                "0______0___\n");

        generator.setRulesetName(ROYALE_RULESET_NAME);
        generator.setTurn(55);
        generator.setHealth("Y", 90);
        generator.setHealth("W", 99);
        generator.setHealth("X", 85);
        generator.setLatency("Y", 83);
        generator.setLatency("W", 72);
        generator.setLatency("X", 175);
        generator.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "HHHHHHHHHHH\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(RIGHT));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_v<<_____0_\n" + //
                "v<_^______v\n" + //
                ">>A^____B<v\n" + //
                "___^____>^v\n" + //
                "yyY^____^<<\n" + //
                "y_>^_______\n" + //
                "yyyyy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 97);
        generator.setHealth("B", 93);
        generator.setHealth("Y", 78);
        generator.setLatency("A", 91);
        generator.setLatency("B", 59);
        generator.setLatency("Y", 86);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(DOWN));
    }

    // same as above but with added fantasy about other snake's options
    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route_plus(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_v<<_____0_\n" + //
                "v<_^<_____v\n" + //
                ">>A_^___B<v\n" + //
                "___>^___>^v\n" + //
                "yyY^____^<<\n" + //
                "y_>^_______\n" + //
                "yyyyy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 97);
        generator.setHealth("B", 93);
        generator.setHealth("Y", 78);
        generator.setLatency("A", 91);
        generator.setLatency("B", 59);
        generator.setLatency("Y", 86);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(DOWN));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_hazard_better_than_lose(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

        AsciiToGameState generator = new AsciiToGameState("" + //
                "_____0_____\n" + //
                "__0____y___\n" + //
                "yyyy__yy___\n" + //
                "yv<yyyy____\n" + //
                "yv^<_______\n" + //
                "yA_^<______\n" + //
                "Y__>^______\n" + //
                "___a_______\n" + //
                "___aa____a_\n" + //
                "0___aaaa_a_\n" + //
                "_______aaa_\n");

        generator.setRulesetName(ROYALE_RULESET_NAME);
        generator.setTurn(174);
        generator.setHealth("A", 89);
        generator.setHealth("Y", 97);
        generator.setLatency("A", 31);
        generator.setLatency("Y", 84);
        generator.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "HHHHHHHHHHH\n");

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ToApiVersion.V1);
        assertThat(move.getMove(), equalToIgnoringCase(DOWN));
    }
}
