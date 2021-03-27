package ru.elynx.battlesnake.testspecific;

import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class TestSnakeDto extends SnakeDto {
    private final ToApiVersion toApiVersion;

    public TestSnakeDto(ToApiVersion toApiVersion) {
        this.toApiVersion = toApiVersion;

        if (this.toApiVersion == ToApiVersion.V0) {
            setLatency(250);
            setSquad("");
        }
    }

    @Override
    public CoordsDto getHead() {
        if (toApiVersion == ToApiVersion.V0) {
            return getBody().get(0);
        }

        return super.getHead();
    }

    @Override
    public void setHead(CoordsDto head) {
        if (toApiVersion == ToApiVersion.V0) {
            throw new UnsupportedOperationException("Test class V0->V1, use setBody");
        }

        super.setHead(head);
    }

    @Override
    public Integer getLength() {
        if (toApiVersion == ToApiVersion.V0) {
            return getBody().size();
        }

        return super.getLength();
    }

    @Override
    public void setLength(Integer length) {
        if (toApiVersion == ToApiVersion.V0) {
            throw new UnsupportedOperationException("Test class V0->V1, use setBody");
        }

        super.setLength(length);
    }
}
