package ru.elynx.battlesnake.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.elynx.battlesnake.engine.GameStrategyBasicTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.protocol.Move.Moves.*;

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
                "____bbb____\n" + //
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
    void test_avoid_fruit_in_corner(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

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
                "____a\n").setHealth("Y", 2);

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
}
