package ru.elynx.battlesnake.entity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

@SpringBootTest
@Tag("Internals")
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
        Move entity = new Move(MoveCommand.UP);
        MoveDto dto = tested.toDto(entity);

        assertEquals("UP", dto.getMove());
        assertNull(dto.getShout());
    }

    @Test
    void test_REPEAT_LAST_throws(@Autowired MoveMapper tested) {
        Move entity = new Move(MoveCommand.REPEAT_LAST, "should throw");

        assertThrows(IllegalArgumentException.class, () -> tested.toDto(entity));
    }
}
