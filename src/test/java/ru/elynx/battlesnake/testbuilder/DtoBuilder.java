package ru.elynx.battlesnake.testbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.elynx.battlesnake.api.GameStateDto;

public class DtoBuilder {
    private DtoBuilder() {
    }

    public static GameStateDto gameStateDto() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ApiExampleBuilder.gameState(), GameStateDto.class);
    }
}
