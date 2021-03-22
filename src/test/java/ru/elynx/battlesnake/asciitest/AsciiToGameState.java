package ru.elynx.battlesnake.asciitest;

import java.util.*;
import ru.elynx.battlesnake.protocol.*;

public class AsciiToGameState {
    // mandatory
    private final String ascii;
    // has some defaults, thus added by "builder" pattern
    // per-snakes
    private Map<String, Integer> healts = new HashMap<>();
    private Map<String, Integer> latencies = new HashMap<>();

    public AsciiToGameState(String ascii) {
        this.ascii = ascii;
    }

    AsciiToGameState setHealth(String name, int health) {
        healts.put(name, health);
        return this;
    }

    AsciiToGameState setLatency(String name, int latency) {
        latencies.put(name, latency);
        return this;
    }

    public GameStateDto build() {
        if (ascii.indexOf('V') >= 0) {
            throw new IllegalStateException("V is not allowed in ascii");
        }

        RulesetDto ruleset = new RulesetDto();
        ruleset.setName("standard");
        ruleset.setVersion("1.0.0");

        GameDto game = new GameDto();
        game.setId("test-game-id");
        game.setRuleset(ruleset);
        game.setTimeout(500);

        GameStateDto gameState = new GameStateDto();
        gameState.setGame(game);
        gameState.setTurn(42);

        BoardDto board = new BoardDto();

        List<String> rows = Arrays.asList(ascii.split("\\r?\\n"));
        rows.removeAll(Arrays.asList("", null));

        final int h = rows.size();
        if (h == 0) {
            throw new IllegalStateException("Could not find rows");
        }

        final int w = rows.get(0).length();
        rows.forEach(s -> {
            if (s.isEmpty() || s.length() != w) {
                throw new IllegalStateException("Rows have invalid size");
            }
        });

        board.setHeight(h);
        board.setWidth(w);

        LinkedList<CoordsDto> food = new LinkedList<>();
        LinkedList<SnakeDto> snakes = new LinkedList<>();
        SnakeDto you = null;
        for (int ww = 0; ww < w; ++ww) {
            for (int hh = 0; hh < h; ++hh) {
                int x = ww;
                int y = w - ww - 1;
                CoordsDto coords = new CoordsDto(x, y);

                char c = rows.get(hh).charAt(ww);

                if (c == '0') {
                    food.add(coords);
                }

                if (c >= 'A' && c <= 'Z') {
                    String s = String.valueOf(c);
                    SnakeDto snake = new SnakeDto();
                    snake.setId(s);
                    snake.setName("Snake " + s);
                    snake.setHealth(healts.getOrDefault(s, 100));
                    snake.setLatency(latencies.getOrDefault(s, 100));
                    snake.setHead(coords);
                    snake.setSquad("Test squad " + s);
                    snake.setShout("Test snake " + s);
                    snake.setBody(new LinkedList<>());

                    // TODO draw the rest of the fucking snake
                    snake.setLength(1);
                    snake.getBody().add(coords);

                    snakes.add(snake);

                    if (c == 'Y') {
                        you = snake;
                    }
                }
            }
        }

        if (you == null || snakes.isEmpty()) {
            throw new IllegalStateException("Not enough snakes or you");
        }

        board.setFood(food);
        board.setHazards(Collections.emptyList());
        board.setSnakes(snakes);

        gameState.setBoard(board);
        gameState.setYou(you);

        return gameState;
    }
}
