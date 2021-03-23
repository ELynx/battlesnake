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
import ru.elynx.battlesnake.testspecific.ApiVersionTranslation;
import ru.elynx.battlesnake.testspecific.TestMove;

@SpringBootTest
class GameStrategyCaseV1Test {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        if (gameStrategy.isPuzzleOnly())
            return;

        System.out.println("Testing " + name);

        // head to head even with snake of same length is lose
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y\n" + //
                "____y\n" + //
                "____Y\n" + //
                "____0\n" + //
                "____A\n" + //
                "____a\n" + //
                "____a\n").setHealth("Y", 10);

        TestMove move = new TestMove(gameStrategy.processMove(generator.build()), ApiVersionTranslation.V1);
        assertThat(move.getMove(), not(equalToIgnoringCase(DOWN)));
    }
}
