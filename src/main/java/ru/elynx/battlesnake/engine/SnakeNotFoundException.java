package ru.elynx.battlesnake.engine;

public class SnakeNotFoundException extends IllegalArgumentException {
    public SnakeNotFoundException(String snakeName) {
        super("Game strategy [" + snakeName + "] is not registered");
    }
}
