package ru.elynx.battlesnake.protocol;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class SnakeDto {
    @NotNull
    @NotEmpty
    private String id;
    @NotNull
    private String name;
    @PositiveOrZero
    private Integer health;
    @NotNull
    private List<CoordsDto> body;
    @PositiveOrZero
    private Integer latency;
    @NotNull
    private CoordsDto head;
    @PositiveOrZero
    private Integer length;
    // TODO constraint
    private String shout;
    // TODO constraint
    private String squad;

    public SnakeDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public List<CoordsDto> getBody() {
        return body;
    }

    public void setBody(List<CoordsDto> body) {
        this.body = body;
    }

    public Integer getLatency() {
        return latency;
    }

    public boolean isTimedOut() {
        if (latency == null)
            return false;

        return latency.equals(0);
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    public CoordsDto getHead() {
        return head;
    }

    public void setHead(CoordsDto head) {
        this.head = head;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getShout() {
        return shout;
    }

    public void setShout(String shout) {
        this.shout = shout;
    }

    public String getSquad() {
        return squad;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }
}
