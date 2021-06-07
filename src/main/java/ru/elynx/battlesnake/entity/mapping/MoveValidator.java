package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.BeforeMapping;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

@Component
public class MoveValidator {
    @BeforeMapping
    public void verifyMoveCommand(Move entity, @TargetType Class<?> targetType) throws IllegalArgumentException {
        if (targetType.equals(MoveDto.class)) {
            verifyMoveCommandForDto(entity.getMoveCommand());
        }
    }

    private void verifyMoveCommandForDto(MoveCommand moveCommand) throws IllegalArgumentException {
        if (!isDtoMove(moveCommand)) {
            throw new IllegalArgumentException("MoveCommand [" + moveCommand + "] cannot be translated into Dto");
        }
    }

    private boolean isDtoMove(MoveCommand moveCommand) {
        // this logic would not get any new fields beside explicitly listed four
        // unless major change in Battlesnake API happens and movement becomes
        // something different from grid-based
        switch (moveCommand) {
            case DOWN :
            case LEFT :
            case RIGHT :
            case UP :
                return true;
            default :
                return false;
        }
    }
}
