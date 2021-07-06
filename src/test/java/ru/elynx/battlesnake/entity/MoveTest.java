package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class MoveTest {
    @Test
    void test_ctor_throws_on_null_move() {
        assertThrows(NullPointerException.class, () -> new Move(null));

        assertThrows(NullPointerException.class, () -> new Move(null, "shout"));
    }

    @Test
    void test_ctor_does_not_throw_on_null_shout() {
        assertDoesNotThrow(() -> new Move(MoveCommand.UP));

        assertDoesNotThrow(() -> new Move(MoveCommand.UP, null));
    }

    @Test
    void test_ctor_assigns() {
        Move move = new Move(MoveCommand.UP, "some shout");

        assertEquals(MoveCommand.UP, move.getMoveCommand());
        assertEquals("some shout", move.getShout());

        move = new Move(MoveCommand.DOWN);

        assertEquals(MoveCommand.DOWN, move.getMoveCommand());
        assertNull(move.getShout());
    }
}
