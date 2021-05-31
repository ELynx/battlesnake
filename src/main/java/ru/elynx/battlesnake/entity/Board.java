package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Value;

@Value
public class Board {
    Dimensions dimensions;

    List<Coordinates> food;
    List<Coordinates> hazards;
    List<Snake> snakes;
}
