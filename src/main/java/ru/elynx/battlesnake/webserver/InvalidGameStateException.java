package ru.elynx.battlesnake.webserver;

public class InvalidGameStateException extends IllegalArgumentException {
    public InvalidGameStateException(String message) {
        super(message);
    }
}
