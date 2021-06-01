package ru.elynx.battlesnake.entity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

class MoveMapperTest {
    @Test
    void test_full_move(@Autowired MoveMapper tested) {
        Move entity = new Move(MoveCommand.UP, "some shout");
        MoveDto dto = tested.toDto(entity);

        assertEquals("UP", dto.getMove());
        assertEquals("some shout", dto.getShout());
    }

    @Test
    void test_move_only(@Autowired MoveMapper tested) {
        // TODO null-less ctor
        Move entity = new Move(MoveCommand.UP, null);
        MoveDto dto = tested.toDto(entity);

        assertEquals("UP", dto.getMove());
        assertNull(dto.getShout());
    }

    @Test
    void test_REPEAT_LAST_throws(@Autowired MoveMapper tested) {
        Move entity = new Move(MoveCommand.REPEAT_LAST, "should throw");

        // TODO define exception
        assertThrows(Exception.class, () -> tested.toDto(entity));
    }
}
