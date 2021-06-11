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
    void test_move_and_shout(@Autowired MoveMapper tested) {
        Move entity = new Move(MoveCommand.UP, "some shout");
        MoveDto dto = tested.toDto(entity);

        assertEquals("up", dto.getMove());
        assertEquals("some shout", dto.getShout());
    }

    @Test
    void test_move(@Autowired MoveMapper tested) {
        Move entity = new Move(MoveCommand.UP);
        MoveDto dto = tested.toDto(entity);

        assertEquals("up", dto.getMove());
        assertNull(dto.getShout());
    }

    @Test
    void test_values_throw_or_not(@Autowired MoveMapper tested) {
        for (MoveCommand moveCommand : MoveCommand.values()) {
            Move entity = new Move(moveCommand);

            switch (moveCommand) {
                case DOWN :
                case LEFT :
                case RIGHT :
                case UP : {
                    assertDoesNotThrow(() -> {
                        MoveDto dto = tested.toDto(entity);
                        assertEquals(moveCommand.toString().toLowerCase(), dto.getMove());
                    });
                }
                    break;
                default :
                    assertThrows(IllegalArgumentException.class, () -> tested.toDto(entity));
                    break;
            }
        }
    }
}
