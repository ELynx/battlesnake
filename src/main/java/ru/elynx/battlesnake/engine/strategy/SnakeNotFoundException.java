package ru.elynx.battlesnake.engine.strategy;

public class SnakeNotFoundException extends IllegalArgumentException {
    public SnakeNotFoundException(String snakeName) {
        super("Game strategy [" + snakeName + "] is not registered");
    }
}
