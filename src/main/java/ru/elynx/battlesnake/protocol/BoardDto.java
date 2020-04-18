package ru.elynx.battlesnake.protocol;

import java.util.List;

public class BoardDto {
    private Integer height;
    private Integer width;
    private List<CoordsDto> food;
    private List<SnakeDto> snakes;

    public BoardDto() {
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

    public List<CoordsDto> getFood() {
        return food;
    }

    public void setFood(List<CoordsDto> food) {
        this.food = food;
    }

    public List<SnakeDto> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<SnakeDto> snakes) {
        this.snakes = snakes;
    }
}
