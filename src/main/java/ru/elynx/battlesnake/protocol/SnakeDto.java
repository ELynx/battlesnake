package ru.elynx.battlesnake.protocol;

import java.util.List;

public class SnakeDto {
    private String id;
    private String name;
    private Integer health;
    private List<CoordsDto> body;
    private String shout;

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

    public String getShout() {
        return shout;
    }

    public void setShout(String shout) {
        this.shout = shout;
    }
}
