package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class Dimensions {
    int width;
    int height;

    public int getArea() {
        return getWidth() * getHeight();
    }

    public boolean isOutOfBounds(Coordinates coordinates) {
        return isOutOfBounds(coordinates.getX(), coordinates.getY());
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

    public Coordinates getCenter() {
        return new Coordinates(width / 2, height / 2);
    }
}
