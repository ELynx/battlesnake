package ru.elynx.battlesnake.testbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.*;

public class EntityBuilder {
    private EntityBuilder() {
    }

    public static HazardPredictor hazardPredictor() {
        return hazardPredictorWithHeadPosition(0, 0);
    }

    public static HazardPredictor hazardPredictorWithHeadPosition(int x, int y) {
        Coordinates head = new Coordinates(x, y);

        List<Coordinates> body = new ArrayList<>();
        body.add(head);

        List<Snake> snakes = new ArrayList<>();
        snakes.add(new Snake("Test I|d", "Test Na|me", 99, body, 250, head, 1, "Test Sh|out", ""));

        String gameId = "Test Ga|me I|d";
        int turn = 0;
        Rules rules = new Rules("standard", "1.234", 500);

        Dimensions dimensions = new Dimensions(15, 11);
        List<Coordinates> food = Collections.emptyList();
        List<Coordinates> hazards = Collections.emptyList();
        Board board = new Board(dimensions, food, hazards, snakes);

        GameState gameState = new GameState(gameId, turn, rules, board, snakes.get(0));
        return new HazardPredictor(gameState, 0);
    }

    public static Rules rulesWithName(String name) {
        return new Rules(name, "1.000", 500);
    }

    public static Snake snakeWithTimeout(Integer latency) {
        Coordinates head = new Coordinates(0, 0);
        List<Coordinates> body = List.of(head);
        return new Snake("Test I|d", "Test Na|me", 99, body, latency, head, body.size(), "Test Sh|out", "");
    }
}
