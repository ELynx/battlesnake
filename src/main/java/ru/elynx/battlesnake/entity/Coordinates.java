package ru.elynx.battlesnake.entity;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Collection;
import java.util.List;
import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class Coordinates {
    public static Coordinates ZERO = new Coordinates(0, 0);

    @With
    int x;
    @With
    int y;

    public CoordinatesWithDirection move(MoveCommand direction) {
        return CoordinatesWithDirection.fromCoordinates(this, direction);
    }

    public Collection<CoordinatesWithDirection> sideNeighbours() {
        return List.of(move(DOWN), move(LEFT), move(RIGHT), move(UP));
    }

    public Collection<Coordinates> cornerNeighbours() {
        return List.of(new Coordinates(getX() - 1, getY() - 1), new Coordinates(getX() + 1, getY() - 1),
                new Coordinates(getX() - 1, getY() + 1), new Coordinates(getX() + 1, getY() + 1));
    }

    public int manhattanDistance(Coordinates other) {
        return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY());
    }
}
