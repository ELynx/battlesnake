package ru.elynx.battlesnake.entity.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.elynx.battlesnake.api.CoordsDto;
import ru.elynx.battlesnake.api.GameStateDto;
import ru.elynx.battlesnake.api.SnakeDto;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

@Mapper(componentModel = "spring")
public interface GameStateMapper {
    @Mapping(target = "gameId", source = "game.id")

    @Mapping(target = "rules.name", source = "game.ruleset.name")
    @Mapping(target = "rules.version", source = "game.ruleset.version")
    @Mapping(target = "rules.timeout", source = "game.timeout")

    @Mapping(target = "rules.royaleHazardDamage", source = "game.ruleset.settings.hazardDamagePerTurn")

    @Mapping(target = "board.dimensions.width", source = "board.width")
    @Mapping(target = "board.dimensions.height", source = "board.height")
    @Mapping(target = "board.activeHazards", ignore = true)
    GameState toEntity(GameStateDto dto);

    @Mapping(target = "withHealth", ignore = true)
    @Mapping(target = "advancingMoves", ignore = true)
    Snake toEntity(SnakeDto dto);

    @Mapping(target = "move", ignore = true)
    @Mapping(target = "withX", ignore = true)
    @Mapping(target = "withY", ignore = true)
    @Mapping(target = "sideNeighbours", ignore = true)
    @Mapping(target = "cornerNeighbours", ignore = true)
    Coordinates toEntity(CoordsDto dto);
}
