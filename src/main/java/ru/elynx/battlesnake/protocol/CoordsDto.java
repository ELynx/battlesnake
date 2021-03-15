package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class CoordsDto {
    @NotNull
    @PositiveOrZero
    private Integer x;
    @NotNull
    @PositiveOrZero
    private Integer y;

    public CoordsDto() {
    }

    public CoordsDto(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "CoordsDto{" + x + ", " + y + '}';
    }
}
