package ru.elynx.battlesnake.protocol;

import java.util.List;

public class Board {
    private Integer height;
    private Integer width;
    private List<Coords> food;
    private List<Snake> snakes;

    public Board() {
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public List<Coords> getFood() {
        return food;
    }

    public void setFood(List<Coords> food) {
        this.food = food;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<Snake> snakes) {
        this.snakes = snakes;
    }
}
