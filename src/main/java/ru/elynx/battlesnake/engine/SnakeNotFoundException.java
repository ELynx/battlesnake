package ru.elynx.battlesnake.engine;

public class SnakeNotFoundException extends IllegalArgumentException {
    public SnakeNotFoundException() {
        super("Game strategy not registered");
    }

    public SnakeNotFoundException(String snakeName) {
        super("Game strategy [" + snakeName + "] is not registered");
    }
}
