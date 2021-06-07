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
    public void validateMove(Move move, @TargetType Class<?> targetType) throws IllegalArgumentException {
        if (targetType.equals(MoveDto.class)) {
            validateMoveForMoveDto(move);
        }
    }

    private void validateMoveForMoveDto(Move move) throws IllegalArgumentException {
        validateMoveCommandForMoveDto(move.getMoveCommand());
    }

    private void validateMoveCommandForMoveDto(MoveCommand moveCommand) throws IllegalArgumentException {
        if (!isDtoMoveCommand(moveCommand)) {
            throw new IllegalArgumentException("MoveCommand [" + moveCommand + "] cannot be translated into Dto");
        }
    }

    private boolean isDtoMoveCommand(MoveCommand moveCommand) {
        switch (moveCommand) {
            // explicitly test for four values that are specified at API level
            case DOWN :
            case LEFT :
            case RIGHT :
            case UP :
                return true;
            // all other (current and future) values are custom
            default :
                return false;
        }
    }
}
