package ru.elynx.battlesnake.asciitest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

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

        GameStateDto dto = tested.setHealth("Y", 99).setLatency("A", 0).build();

        assertNotNull(dto.getGame());
        assertNotNull(dto.getGame().getId());
        assertNotNull(dto.getGame().getRuleset());
        assertNotNull(dto.getGame().getRuleset().getName());
        assertNotNull(dto.getGame().getRuleset().getVersion());
        assertNotNull(dto.getGame().getTimeout());

        assertNotNull(dto.getTurn());

        assertNotNull(dto.getBoard());
        assertNotNull(dto.getBoard().getHeight());
        assertNotNull(dto.getBoard().getWidth());
        assertNotNull(dto.getBoard().getFood());
        assertNotNull(dto.getBoard().getHazards());
        assertNotNull(dto.getBoard().getSnakes());

        final int h = dto.getBoard().getHeight();
        final int w = dto.getBoard().getWidth();

        Consumer<CoordsDto> verifyCoord = (CoordsDto coords) -> {
            assertNotNull(coords.getX());
            assertNotNull(coords.getY());

            assertTrue(0 <= coords.getX() && coords.getX() < w);
            assertTrue(0 <= coords.getY() && coords.getX() < h);
        };

        dto.getBoard().getFood().forEach(verifyCoord);
        dto.getBoard().getHazards().forEach(verifyCoord);

        Consumer<SnakeDto> verifySnake = (SnakeDto snake) -> {
            assertNotNull(snake.getId());
            assertNotNull(snake.getName());
            assertNotNull(snake.getHealth());
            assertNotNull(snake.getBody());
            snake.getBody().forEach(verifyCoord);
            assertNotNull(snake.getLatency());
            assertNotNull(snake.getHead());
            verifyCoord.accept(snake.getHead());
            assertEquals(snake.getHead(), snake.getBody().get(0));
            assertNotNull(snake.getLength());
            assertEquals(snake.getLength(), snake.getBody().size());
            assertNotNull(snake.getShout());
            assertNotNull(snake.getSquad());
        };

        dto.getBoard().getSnakes().forEach(verifySnake);

        assertNotNull(dto.getYou());
        verifySnake.accept(dto.getYou());
    }

    @Test
    void test_food() {
        // a comment a day keeps a spotless away
        AsciiToGameState tested = new AsciiToGameState("" + //
                "_00_________Y\n" + //
                "___________0_\n" + //
                "0____________\n");

        GameStateDto dto = tested.build();
        assertEquals(13, dto.getBoard().getWidth());
        assertEquals(3, dto.getBoard().getHeight());

        List<CoordsDto> expectedFood = new ArrayList<>();
        expectedFood.add(new CoordsDto(0, 0));
        expectedFood.add(new CoordsDto(11, 1));
        expectedFood.add(new CoordsDto(1, 2));
        expectedFood.add(new CoordsDto(2, 2));

        System.out.print("Got food: ");
        System.out.println(dto.getBoard().getFood());
        System.out.print("Expected: ");
        System.out.println(expectedFood);

        assertThat(dto.getBoard().getFood(), Matchers.containsInAnyOrder(expectedFood.toArray()));
    }
}
