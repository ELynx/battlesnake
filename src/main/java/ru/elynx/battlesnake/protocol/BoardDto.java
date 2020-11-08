package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public class BoardDto {
    @NotNull
    @Positive
    private Integer height;
    @NotNull
    @Positive
    private Integer width;
    @NotNull
    private List<CoordsDto> food;
    @NotNull
    private List<CoordsDto> hazards;
    @NotNull
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

    public List<CoordsDto> getHazards() {
        return hazards;
    }

    public void setHazards(List<CoordsDto> hazards) {
        this.hazards = hazards;
    }

    public List<SnakeDto> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<SnakeDto> snakes) {
        this.snakes = snakes;
    }
}
