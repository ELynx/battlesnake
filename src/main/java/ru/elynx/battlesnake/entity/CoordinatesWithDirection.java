package ru.elynx.battlesnake.entity;

import lombok.Getter;

public class CoordinatesWithDirection extends Coordinates {
    @Getter
    private final MoveCommand direction;

    private CoordinatesWithDirection(int x, int y, MoveCommand direction) {
        super(x, y);
        this.direction = direction;
    }

    public static CoordinatesWithDirection fromCoordinates(Coordinates from, MoveCommand direction) {
        switch (direction) {
            case DOWN :
                return new CoordinatesWithDirection(from.getX(), from.getY() - 1, direction);
            case LEFT :
                return new CoordinatesWithDirection(from.getX() - 1, from.getY(), direction);
            case RIGHT :
                return new CoordinatesWithDirection(from.getX() + 1, from.getY(), direction);
            case UP :
                return new CoordinatesWithDirection(from.getX(), from.getY() + 1, direction);
            default :
                // https://github.com/jacoco/jacoco/wiki/FilteringOptions#filters-for-code-where-test-execution-is-questionable-or-impossible-by-design
                throw new IllegalArgumentException("MoveCommand [" + direction + "] is not a supported direction");
        }
    }

    @Override
    public boolean equals(Object to) {
        return super.equals(to);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
