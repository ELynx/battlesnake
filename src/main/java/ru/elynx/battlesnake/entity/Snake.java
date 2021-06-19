package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Snake {
    private static final int MAX_HEALTH = 100;

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

    public boolean isTimedOut() {
        if (latency == null)
            return false;

        return latency.equals(0);
    }

    /**
     * Predict if snake will grow on it's tail this turn
     *
     * @return True if snake will not empty it's tail cell.
     */
    public boolean isGrowing() {
        // just ate food
        if (health == MAX_HEALTH) {
            return true;
        }

        if (body.size() > 1) {
            Coordinates last = body.get(body.size() - 1);
            Coordinates preLast = body.get(body.size() - 2);
            return last.equals(preLast);
        }

        return false;
    }
}
