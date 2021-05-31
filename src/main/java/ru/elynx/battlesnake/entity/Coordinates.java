package ru.elynx.battlesnake.entity;

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
            case UP :
                return withY(getY() + 1);
            case RIGHT :
                return withX(getX() + 1);
            case DOWN :
                return withY(getY() - 1);
            case LEFT :
                return withX(getX() - 1);
            default :
                throw new IllegalArgumentException("MoveCommand [" + moveCommand + "] is not a supported direction");
        }
    }
}
