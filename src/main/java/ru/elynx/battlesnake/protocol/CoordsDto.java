package ru.elynx.battlesnake.protocol;

public class CoordsDto {
    private Integer x;
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
}
