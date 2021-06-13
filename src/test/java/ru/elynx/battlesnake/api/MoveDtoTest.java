package ru.elynx.battlesnake.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MoveDtoTest {
    @Test
    void test_ctor_move_not_null_check() {
        assertThrows(NullPointerException.class, () -> {
            MoveDto moveDto = new MoveDto(null, "shout");
        });
    }

    @Test
    void test_ctor_move_can_be_null() {
        assertDoesNotThrow(() -> {
            MoveDto moveDto = new MoveDto("UP", null);
        });
    }

    @Test
    void test_ctor_lowercase_move() {
        MoveDto moveDto = new MoveDto("DOWN", null);
        assertEquals("down", moveDto.getMove());
    }
}
