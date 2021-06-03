package ru.elynx.battlesnake.entity;

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

    public Iterable<Coordinates> sideNeighbours() {
        return List.of(move(MoveCommand.DOWN), move(MoveCommand.LEFT), move(MoveCommand.RIGHT), move(MoveCommand.UP));
    }

    public Iterable<Coordinates> angleNeighbours() {
        return List.of(new Coordinates(getX() - 1, getY() - 1), new Coordinates(getX() + 1, getY() - 1),
                new Coordinates(getX() - 1, getY() + 1), new Coordinates(getX() + 1, getY() + 1));
    }

    public boolean within(Dimensions dimensions) {
        return getX() >= 0 && getY() >= 0 && getX() < dimensions.getWidth() && getY() < dimensions.getHeight();
    }

    public int manhattanDistance(Coordinates other) {
        return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY());
    }
}
