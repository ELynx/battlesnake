package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Board {
    @NonNull
    Dimensions dimensions;

    @NonNull
    List<Coordinates> food;
    @NonNull
    List<Coordinates> hazards;
    @NonNull
    List<Snake> snakes;
}
