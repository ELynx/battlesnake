package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.*;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

@Mapper(componentModel = "spring")
public interface MoveMapper {
    @Mapping(target = "move", source = "moveCommand")
    MoveDto toDto(Move entity);

    @ValueMapping(target = "down", source = "DOWN")
    @ValueMapping(target = "left", source = "LEFT")
    @ValueMapping(target = "right", source = "RIGHT")
    @ValueMapping(target = "up", source = "UP")
    @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_UNMAPPED)
    String moveCommandToMove(MoveCommand moveCommand);
}
