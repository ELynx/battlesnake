package ru.elynx.battlesnake.engine;

public class SnakeNotFoundException extends IllegalArgumentException {
    public SnakeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
