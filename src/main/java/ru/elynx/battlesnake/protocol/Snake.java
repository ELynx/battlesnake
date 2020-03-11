package ru.elynx.battlesnake.protocol;

import java.util.List;

public class Snake {
    private String id;
    private String name;
    private Integer health;
    private List<Coords> body;
    private String shout;

    public Snake() {
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

    public List<Coords> getBody() {
        return body;
    }

    public void setBody(List<Coords> body) {
        this.body = body;
    }

    public String getShout() {
        return shout;
    }

    public void setShout(String shout) {
        this.shout = shout;
    }
}
