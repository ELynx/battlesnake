package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class Dimensions {
    int width;
    int height;

    public int area() {
        return getWidth() * getHeight();
    }

    public boolean outOfBounds(Coordinates coordinates) {
        return outOfBounds(coordinates.getX(), coordinates.getY());
    }

    public boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }
}
