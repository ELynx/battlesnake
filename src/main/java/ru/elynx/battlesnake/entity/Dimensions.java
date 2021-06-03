package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class Dimensions {
    int width;
    int height;

    public int area() {
        return getWidth() * getHeight();
    }
}
