package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.entity.Move;

@Mapper(componentModel = "spring", uses = MoveValidator.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MoveMapper {
    @Mapping(target = "move", source = "moveCommand")
    MoveDto toDto(Move entity);
}
