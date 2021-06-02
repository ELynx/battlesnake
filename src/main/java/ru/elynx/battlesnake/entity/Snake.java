package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Snake {
    @NonNull
    String id;
    @NonNull
    String name;

    int health;

    @NonNull
    List<Coordinates> body;

    Integer latency;

    @NonNull
    Coordinates head;
    @NonNull
    Integer length;

    String shout;

    String squad;

    // TODO unit test
    public boolean isTimedOut() {
        if (latency == null)
            return false;

        return latency.equals(0);
    }
}
