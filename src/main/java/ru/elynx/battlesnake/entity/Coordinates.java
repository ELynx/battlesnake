package ru.elynx.battlesnake.entity;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Collection;
import java.util.List;
import lombok.Value;
import lombok.With;

@Value
public class Coordinates {
    @With
    int x;
    @With
    int y;

    public Coordinates move(MoveCommand moveCommand) {
        switch (moveCommand) {
            case DOWN :
                return withY(getY() - 1);
            case LEFT :
                return withX(getX() - 1);
            case RIGHT :
                return withX(getX() + 1);
            case UP :
                return withY(getY() + 1);
            default :
                throw new IllegalArgumentException("MoveCommand [" + moveCommand + "] is not a supported direction");
        }
    }

    public Collection<Coordinates> sideNeighbours() {
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
