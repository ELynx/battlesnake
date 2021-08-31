package ru.elynx.battlesnake.testbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.elynx.battlesnake.entity.*;

public class EntityBuilder {
    private static final int DEFAULT_SNAKE_HEAD_X = 0;
    private static final int DEFAULT_SNAKE_HEAD_Y = 0;
    private static final String DEFAULT_SNAKE_ID = "Test I|d";
    private static final String DEFAULT_SNAKE_NAME = "Test Na|me";
    private static final Integer DEFAULT_SNAKE_LATENCY = 250;

    private EntityBuilder() {
    }

    public static GameState gameState() {
        return gameStateWithYouSnake(buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID,
                DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY));
    }

    public static GameState gameStateWithHeadPosition(int x, int y) {
        return gameStateWithYouSnake(snakeWithHead(x, y));
    }

    public static GameState gameStateWithName(String name) {
        return gameStateWithYouSnake(snakeWithName(name));
    }

    private static GameState gameStateWithYouSnake(Snake you) {
        List<Snake> snakes = new ArrayList<>();
        snakes.add(you);

        String gameId = "Test Ga|me I|d";
        int turn = 0;
        Rules rules = new Rules(ApiExampleBuilder.standardRulesetName(), "1.234", 500, 15);

        Dimensions dimensions = new Dimensions(15, 11);
        List<Coordinates> food = Collections.emptyList();
        List<Coordinates> hazards = Collections.emptyList();
        Board board = new Board(dimensions, food, hazards, snakes);

        return new GameState(gameId, turn, rules, board, snakes.get(0));
    }

    public static Rules rulesWithName(String name) {
        return new Rules(name, "1.000", 500, 15);
    }

    private static Snake snakeWithHead(int x, int y) {
        return buildSnake(x, y, DEFAULT_SNAKE_ID, DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY);
    }

    private static Snake snakeWithName(String name) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID, name, DEFAULT_SNAKE_LATENCY);
    }

    public static Snake snakeWithLatency(Integer latency) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID, DEFAULT_SNAKE_NAME, latency);
    }

    public static Snake snakeWithId(String id) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, id, DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY);
    }

    private static Snake buildSnake(int x, int y, String id, String name, Integer latency) {
        Coordinates head = new Coordinates(x, y);

        List<Coordinates> body = new ArrayList<>();
        body.add(head);

        return new Snake(id, name, 99, body, latency, head, 1, "Test Sh|out", null);
    }
}
