package ru.elynx.battlesnake.testspecific;

import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class TestSnakeDto extends SnakeDto {
    private final ApiVersionTranslation apiVersionTranslation;

    public TestSnakeDto(ApiVersionTranslation apiVersionTranslation) {
        this.apiVersionTranslation = apiVersionTranslation;

        if (this.apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            setLatency(250);
            setSquad("");
        }
    }

    @Override
    public CoordsDto getHead() {
        if (apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            return getBody().get(0);
        }

        return super.getHead();
    }

    @Override
    public void setHead(CoordsDto head) {
        if (apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            throw new UnsupportedOperationException("Test class V0->V1, use setBody");
        }

        super.setHead(head);
    }

    @Override
    public Integer getLength() {
        if (apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            return getBody().size();
        }

        return super.getLength();
    }

    @Override
    public void setLength(Integer length) {
        if (apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            throw new UnsupportedOperationException("Test class V0->V1, use setBody");
        }

        super.setLength(length);
    }
}
