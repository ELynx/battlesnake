package ru.elynx.battlesnake.testbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.elynx.battlesnake.entity.*;

@UtilityClass
public class EntityBuilder {
    private static final int DEFAULT_SNAKE_HEAD_X = 0;
    private static final int DEFAULT_SNAKE_HEAD_Y = 0;
    private static final String DEFAULT_SNAKE_ID = "Test I|d";
    private static final String DEFAULT_SNAKE_NAME = "Test Na|me";
    private static final Integer DEFAULT_SNAKE_LATENCY = 250;

    public GameState gameState() {
        return gameStateWithYouSnake(buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID,
                DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY));
    }

    public GameState gameStateLoss() {
        Snake you = buildSnake(0, 0, "you", "You Snake", DEFAULT_SNAKE_LATENCY);
        Snake winner = buildSnake(1, 1, "winner", "Winner Snake", DEFAULT_SNAKE_LATENCY);

        List<Snake> snakes = new ArrayList<>();
        snakes.add(winner);

        return gameStateWithYouAndAll(you, snakes);
    }

    public GameState gameStateWithHeadPosition(int x, int y) {
        return gameStateWithYouSnake(snakeWithHead(x, y));
    }

    public GameState gameStateWithName(String name) {
        return gameStateWithYouSnake(snakeWithName(name));
    }

    private GameState gameStateWithYouSnake(Snake you) {
        List<Snake> snakes = new ArrayList<>();
        snakes.add(you);

        return gameStateWithYouAndAll(you, snakes);
    }

    private GameState gameStateWithYouAndAll(Snake you, List<Snake> all) {
        String gameId = "Test Ga|me I|d";
        int turn = 0;
        Rules rules = new Rules(ApiExampleBuilder.standardRulesetName(), "1.234", 500, 15);

        Dimensions dimensions = new Dimensions(15, 11);
        List<Coordinates> food = Collections.emptyList();
        List<Coordinates> hazards = Collections.emptyList();
        Board board = new Board(dimensions, food, hazards, all);

        return new GameState(gameId, turn, rules, board, you);
    }

    public Rules rulesWithName(String name) {
        return new Rules(name, "1.000", 500, 15);
    }

    private Snake snakeWithHead(int x, int y) {
        return buildSnake(x, y, DEFAULT_SNAKE_ID, DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY);
    }

    private Snake snakeWithName(String name) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID, name, DEFAULT_SNAKE_LATENCY);
    }

    public Snake snakeWithLatency(Integer latency) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, DEFAULT_SNAKE_ID, DEFAULT_SNAKE_NAME, latency);
    }

    public Snake snakeWithId(String id) {
        return buildSnake(DEFAULT_SNAKE_HEAD_X, DEFAULT_SNAKE_HEAD_Y, id, DEFAULT_SNAKE_NAME, DEFAULT_SNAKE_LATENCY);
    }

    private Snake buildSnake(int x, int y, String id, String name, Integer latency) {
        Coordinates head = new Coordinates(x, y);

        List<Coordinates> body = new ArrayList<>();
        body.add(head);

        return new Snake(id, name, 99, body, latency, head, 1, "Test Sh|out", null);
    }
}
