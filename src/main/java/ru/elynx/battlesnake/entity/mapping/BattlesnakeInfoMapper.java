package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.elynx.battlesnake.api.BattlesnakeInfoDto;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;

@Mapper(componentModel = "spring")
public interface BattlesnakeInfoMapper {
    @Mapping(target = "apiversion", constant = "1")
    BattlesnakeInfoDto toDto(BattlesnakeInfo entity);
}
