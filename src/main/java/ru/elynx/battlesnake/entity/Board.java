package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class Board {
    @NonNull
    Dimensions dimensions;

    @NonNull
    List<Coordinates> food;
    @NonNull
    List<Coordinates> hazards;
    @NonNull
    List<Snake> snakes;

    /**
     * Due to quirk of implementation, hazards do not damage snake health on turn
     * they appear.
     *
     * @return hazards that currently damage snake health.
     */
    public List<Coordinates> getActiveHazards() {
        return hazards;
    }

    /**
     * Due to quirk of implementation, hazards do not damage snake health on turn
     * they appear.
     *
     * @return If there are hazards that currently do not do damage.
     */
    public boolean hasInactiveHazards() {
        return false;
    }
}
