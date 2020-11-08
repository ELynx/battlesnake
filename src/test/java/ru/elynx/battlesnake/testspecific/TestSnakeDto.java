package ru.elynx.battlesnake.testspecific;

import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class TestSnakeDto extends SnakeDto {
    public TestSnakeDto() {
    }

    public TestSnakeDto(ApiVersionTranslation apiVersionTranslation) {
        if (apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            setLatency(250);
            setSquad("");
        }
    }

    @Override
    public CoordsDto getHead() {
        return getBody().get(0);
    }

    @Override
    public void setHead(CoordsDto body) {
        throw new RuntimeException("Test class, use setBody");
    }

    @Override
    public Integer getLength() {
        return getBody().size();
    }

    @Override
    public void setLength(Integer length) {
        throw new RuntimeException("Test class, use setBody");
    }

    public enum ApiVersionTranslation {
        V0_TO_V1,
        V1
    }
}
