package ru.elynx.battlesnake.entity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

@SpringBootTest
class MoveValidatorTest {
    @Test
    void test_values_throw_or_not(@Autowired MoveValidator tested) {
        for (MoveCommand moveCommand : MoveCommand.values()) {
            Move entity = new Move(moveCommand);

            switch (moveCommand) {
                case DOWN :
                case LEFT :
                case RIGHT :
                case UP :
                    assertDoesNotThrow(() -> tested.validateMove(entity, MoveDto.class));
                    break;
                default :
                    assertThrows(IllegalArgumentException.class, () -> tested.validateMove(entity, MoveDto.class));
                    break;
            }
        }
    }
}
