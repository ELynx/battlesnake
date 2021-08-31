package ru.elynx.battlesnake.testsnake;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("TestComponent")
class MySnakeTest {
    @Test
    void test_battlesnake_info() {
        MySnake tested = new MySnake();
        assertNotNull(tested.getBattesnakeInfo());

        assertNotNull(tested.getBattesnakeInfo().getAuthor());

        assertNotNull(tested.getBattesnakeInfo().getColor());
        assertNotNull(tested.getBattesnakeInfo().getHead());
        assertNotNull(tested.getBattesnakeInfo().getTail());

        assertNotNull(tested.getBattesnakeInfo().getVersion());
    }

    @Test
    void test_process_start_does_not_throw() {
        MySnake tested = new MySnake();

        assertDoesNotThrow(() -> {
            tested.processStart(EntityBuilder.gameState());
        });
    }

    @Test
    void test_process_move_does_not_throw() {
        MySnake tested = new MySnake();

        assertDoesNotThrow(() -> {
            tested.processMove(EntityBuilder.gameState());
        });
    }

    @Test
    void test_process_end_does_not_throw() {
        MySnake tested = new MySnake();

        assertDoesNotThrow(() -> {
            tested.processEnd(EntityBuilder.gameState());
        });
    }

    @Test
    void test_process_move_hardcoded() {
        MySnake tested = new MySnake();
        assertEquals(MoveCommand.UP, tested.processMove(EntityBuilder.gameState()).orElseThrow());
    }
}
