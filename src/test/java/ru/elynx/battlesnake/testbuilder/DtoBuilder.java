package ru.elynx.battlesnake.testbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.elynx.battlesnake.api.GameStateDto;

@UtilityClass
public class DtoBuilder {
    public GameStateDto gameStateDto() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ApiExampleBuilder.gameState(), GameStateDto.class);
    }
}
