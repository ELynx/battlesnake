package ru.elynx.battlesnake.asciitest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.ApiExampleBuilder;

@Tag("TestComponent")
class AsciiToGameStateTest {
    @Test
    void smoke_test() {
        // a comment a day keeps a spotless away
        AsciiToGameState tested = new AsciiToGameState("" + //
                "_yyy_______\n" + //
                "___Y___0___\n" + //
                "___________\n" + //
                "_____A_____\n" + //
                "_______0___\n" + //
                "_____B_____\n" + //
                "_____b_____\n" + //
                "____Cb_____\n" + //
                "____^^v<<__\n" + //
                "____^^>>v__\n" + //
                "___>^^<<<__\n");

        GameState entity = tested.setTurn(123).setRulesetName(ApiExampleBuilder.royaleRulesetName())
                .setStartSnakeLength(4).setHealth("Y", 99).setLatency("A", 0).setHazards("" + //
                        "HHHHHHHHHHH\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "H_________H\n" + //
                        "HHHHHHHHHHH\n")
                .build();

        assertNotNull(entity.getGameId());
        assertNotNull(entity.getRules());
        assertNotNull(entity.getRules().getName());
        assertNotNull(entity.getRules().getVersion());
        assertTrue(0 <= entity.getRules().getRoyaleHazardDamage(), "Non-negative hazard damage");

        assertNotNull(entity.getBoard());
        assertNotNull(entity.getBoard().getDimensions());
        assertNotNull(entity.getBoard().getFood());
        assertNotNull(entity.getBoard().getHazards());
        assertNotNull(entity.getBoard().getSnakes());

        int h = entity.getBoard().getDimensions().getHeight();
        int w = entity.getBoard().getDimensions().getWidth();

        Consumer<Coordinates> verifyCoordinates = (Coordinates coordinates) -> {
            assertTrue(0 <= coordinates.getX() && coordinates.getX() < w);
            assertTrue(0 <= coordinates.getY() && coordinates.getX() < h);
        };

        entity.getBoard().getFood().forEach(verifyCoordinates);
        entity.getBoard().getHazards().forEach(verifyCoordinates);

        Consumer<Snake> verifySnake = (Snake snake) -> {
            assertNotNull(snake.getId());
            assertNotNull(snake.getName());
            assertNotNull(snake.getBody());
            snake.getBody().forEach(verifyCoordinates);
            assertNotNull(snake.getLatency());
            assertNotNull(snake.getHead());
            verifyCoordinates.accept(snake.getHead());
            assertEquals(snake.getHead(), snake.getBody().get(0));
            assertTrue(0 < snake.getLength(), "Snake has positive length");
            assertEquals(snake.getLength(), snake.getBody().size());
            assertNotNull(snake.getShout());
            assertNotNull(snake.getSquad());
        };

        entity.getBoard().getSnakes().forEach(verifySnake);

        assertNotNull(entity.getYou());
        verifySnake.accept(entity.getYou());
    }

    @Test
    void test_food() {
        // a comment a day keeps a spotless away
        AsciiToGameState tested = new AsciiToGameState("" + //
                "_00_________Y\n" + //
                "___________0_\n" + //
                "0____________\n");

        GameState entity = tested.build();
        assertEquals(13, entity.getBoard().getDimensions().getWidth());
        assertEquals(3, entity.getBoard().getDimensions().getHeight());

        assertThat(entity.getBoard().getFood(), containsInAnyOrder(new Coordinates(0, 0), new Coordinates(11, 1),
                new Coordinates(1, 2), new Coordinates(2, 2)));
    }

    @Test
    void test_turn() {
        AsciiToGameState tested = new AsciiToGameState("Y");

        GameState entity = tested.setTurn(234).build();

        assertEquals(234, entity.getTurn());
    }

    @Test
    void test_ruleset_name() {
        AsciiToGameState tested = new AsciiToGameState("Y");

        GameState entity = tested.setRulesetName("qwerty").build();

        assertEquals("qwerty", entity.getRules().getName());
    }

    private static Snake getSnakeOrNull(GameState gameState, String snakeName) {
        return gameState.getBoard().getSnakes().stream().filter(snake -> snake.getId().equals(snakeName)).findAny()
                .orElse(null);
    }

    @Test
    void test_start_snake_length() {
        AsciiToGameState tested = new AsciiToGameState("YABC");

        GameState entity = tested.setStartSnakeLength(11).build();

        assertEquals(4, entity.getBoard().getSnakes().size());

        for (Snake snake : entity.getBoard().getSnakes()) {
            assertEquals(11, snake.getLength());
        }
    }

    @Test
    void test_snake_length() {
        AsciiToGameState tested = new AsciiToGameState("YABC");

        GameState entity = tested.setLength("Y", 11).setLength("A", 11).setLength("B", 11).setLength("C", 11).build();

        assertEquals(4, entity.getBoard().getSnakes().size());

        for (Snake snake : entity.getBoard().getSnakes()) {
            assertEquals(11, snake.getLength());
        }
    }

    @Test
    void test_hazard() {
        AsciiToGameState tested = new AsciiToGameState("" + //
                "Y__________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n");

        GameState entity = tested.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n").build();

        assertEquals(55, entity.getBoard().getHazards().size());
        assertEquals(ApiExampleBuilder.royaleRulesetName(), entity.getRules().getName());

        entity = tested.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "H_________H\n" + //
                "H_________H\n" + //
                "H_________H\n" + //
                "HHHHHHHHHHH\n").build();

        assertEquals(28, entity.getBoard().getHazards().size());
        assertEquals(ApiExampleBuilder.royaleRulesetName(), entity.getRules().getName());
    }

    @Test
    void test_hazard_damage() {
        AsciiToGameState tested = new AsciiToGameState("Y");

        GameState entity = tested.setHazardDamage(345).build();

        assertEquals(345, entity.getRules().getRoyaleHazardDamage());
    }

    @Test
    void test_health_and_latency() {
        AsciiToGameState tested = new AsciiToGameState("YABC");

        GameState entity = tested //
                .setHealth("Y", 99) //
                .setHealth("A", 10) //
                .setHealth("B", 15) //
                .setLatency("Y", 499) //
                .setLatency("A", 99) //
                .setLatency("C", 0) //
                .build();

        assertEquals(4, entity.getBoard().getSnakes().size());

        Snake y = getSnakeOrNull(entity, "Y");
        assertEquals(99, y.getHealth());
        assertEquals(499, y.getLatency());

        Snake a = getSnakeOrNull(entity, "A");
        assertEquals(10, a.getHealth());
        assertEquals(99, a.getLatency());

        Snake b = getSnakeOrNull(entity, "B");
        assertEquals(15, b.getHealth());
        assertThat(b.getLatency(), greaterThanOrEqualTo(0));

        Snake c = getSnakeOrNull(entity, "C");
        assertThat(c.getHealth(), is(both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo(Snake.getMaxHealth()))));
        assertEquals(0, c.getLatency());
    }

    @Test
    void test_snake_parsing() {
        AsciiToGameState tested = new AsciiToGameState("" + //
                "Yyyyyyyyyyy\n" + //
                "aaaaaaaaaaA\n" + //
                "Bbbbbbbbbb<\n" + //
                "_________>^\n" + //
                "bbbbbbbbb^_\n" + //
                "C<<<<<<<<<<\n" + //
                "v<<<<<<<<<^\n" + //
                ">>>>>>>>>>^\n");

        GameState entity = tested.build();

        assertEquals(4, entity.getBoard().getSnakes().size());

        Snake y = getSnakeOrNull(entity, "Y");
        assertEquals(11, y.getLength());

        Snake a = getSnakeOrNull(entity, "A");
        assertEquals(11, a.getLength());

        Snake b = getSnakeOrNull(entity, "B");
        assertEquals(23, b.getLength());

        Snake c = getSnakeOrNull(entity, "C");
        assertEquals(33, c.getLength());
    }
}
