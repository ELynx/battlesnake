package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;

@Mapper(componentModel = "spring")
public interface MoveMapper {
    @Mapping(source = "moveCommand", target = "move")
    MoveDto toDto(Move entity);
}
