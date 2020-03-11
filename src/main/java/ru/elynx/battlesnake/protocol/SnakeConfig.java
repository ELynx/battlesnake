package ru.elynx.battlesnake.protocol;

public class SnakeConfig {
    public static SnakeConfig DEFAULT_SNAKE_CONFIG = new SnakeConfig("#ffbf00", "smile", "regular");

    private String color; // TODO color
    private String headType; // TODO enum
    private String tailType; // TODO enum

    public SnakeConfig() {
    }

    public SnakeConfig(String color, String headType, String tailType) {
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
