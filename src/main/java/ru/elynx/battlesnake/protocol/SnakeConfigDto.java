package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SnakeConfigDto {
    @Pattern(regexp = "#[[:xdigit:]]{6}")
    private String color;
    @NotEmpty
    private String headType;
    @NotEmpty
    private String tailType;

    public SnakeConfigDto() {
    }

    public SnakeConfigDto(String color, String headType, String tailType) {
        this.color = color;
        this.headType = headType;
        this.tailType = tailType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHeadType() {
        return headType;
    }

    public void setHeadType(String headType) {
        this.headType = headType;
    }

    public String getTailType() {
        return tailType;
    }

    public void setTailType(String tailType) {
        this.tailType = tailType;
    }
}
