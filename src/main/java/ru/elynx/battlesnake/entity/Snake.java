package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Value;

@Value
public class Snake {
    String id;
    String name;

    int health;

    List<Coordinates> body;

    Integer latency;

    Coordinates head;
    Integer length;

    String shout;

    String squad;

    public boolean isTimedOut() {
        if (latency == null)
            return false;

        return latency.equals(0);
    }
}
