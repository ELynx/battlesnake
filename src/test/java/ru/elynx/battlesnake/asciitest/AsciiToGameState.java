package ru.elynx.battlesnake.asciitest;

import java.util.*;
import java.util.function.Function;
import org.javatuples.KeyValue;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.*;
import ru.elynx.battlesnake.testspecific.TestSnakeDto;
import ru.elynx.battlesnake.testspecific.ToApiVersion;

public class AsciiToGameState {
    // mandatory
    private final String ascii;
    // has some defaults, thus added by "builder" pattern
    private int startSnakeSize = 3;
    // per-snakes
    private Map<String, Integer> healts = new HashMap<>();
    private Map<String, Integer> latencies = new HashMap<>();

    public AsciiToGameState(String ascii) {
        this.ascii = ascii;
    }

    public AsciiToGameState setStartSnakeSize(int startSnakeSize) {
        this.startSnakeSize = startSnakeSize;
        return this;
    }

    public AsciiToGameState setHealth(String name, int health) {
        healts.put(name, health);
        return this;
    }

    public AsciiToGameState setLatency(String name, int latency) {
        latencies.put(name, latency);
        return this;
    }

    private List<KeyValue<CoordsDto, Character>> getNeighbours(List<String> rows, int height, int width,
            List<CoordsDto> soFar, CoordsDto center, char lookupChar) {
        LinkedList<KeyValue<CoordsDto, Character>> result = new LinkedList<>();

        Function<KeyValue<CoordsDto, Character>, Void> addIfChecksUp = pair -> {
            CoordsDto coords = pair.getKey();

            // avoid already found pieces
            if (soFar.indexOf(coords) >= 0) {
                return null;
            }

            if (coords.getX() >= 0 && coords.getX() < width && coords.getY() >= 0 && coords.getY() < height) {

                int row = height - coords.getY() - 1;
                int index = coords.getX();

                char c = rows.get(row).charAt(index);

                // if snake letter or direction came up
                if (lookupChar == c || pair.getValue().equals(c)) {
                    // make sure to pass what actually was on the ascii
                    result.add(new KeyValue<>(coords, c));
                }
            }

            return null;
        };

        int x = center.getX();
        int y = center.getY();

        int xleft = x - 1;
        int xright = x + 1;
        int ydown = y - 1;
        int yup = y + 1;

        // arrow pointing to center
        addIfChecksUp.apply(new KeyValue<>(new CoordsDto(xleft, y), '>'));
        addIfChecksUp.apply(new KeyValue<>(new CoordsDto(x, yup), 'v'));
        addIfChecksUp.apply(new KeyValue<>(new CoordsDto(xright, y), '<'));
        addIfChecksUp.apply(new KeyValue<>(new CoordsDto(x, ydown), '^'));

        return result;
    }

    private CoordsDto getNextSnakeCoordsDtoOrNull(List<String> rows, int height, int width, List<CoordsDto> soFar,
            CoordsDto current, char bodyChar) {
        List<KeyValue<CoordsDto, Character>> neighbours = getNeighbours(rows, height, width, soFar, current, bodyChar);

        // priority 1 - arrows pointing
        for (KeyValue<CoordsDto, Character> keyValue : neighbours) {
            if ("<^>v".indexOf(keyValue.getValue()) >= 0) {
                return keyValue.getKey();
            }
        }

        // priority 2 - body characters
        // there are no other trigger conditions, get 1st or null
        if (neighbours.isEmpty()) {
            return null;
        }

        return neighbours.get(0).getKey();
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

        GameStateDto gameState = new GameStatePredictor();
        gameState.setGame(game);
        gameState.setTurn(42);

        BoardDto board = new BoardDto();

        List<String> rows = Arrays.asList(ascii.split("\\r?\\n"));
        rows.removeAll(Arrays.asList("", null));

        final int height = rows.size();
        if (height == 0) {
            throw new IllegalStateException("Could not find rows");
        }

        final int width = rows.get(0).length();
        rows.forEach(s -> {
            if (s.isEmpty() || s.length() != width) {
                throw new IllegalStateException("Rows have invalid size");
            }
        });

        board.setHeight(height);
        board.setWidth(width);

        LinkedList<CoordsDto> food = new LinkedList<>();
        LinkedList<SnakeDto> snakes = new LinkedList<>();
        SnakeDto you = null;
        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                int x = w;
                int y = height - h - 1;
                CoordsDto coords = new CoordsDto(x, y);

                char c = rows.get(h).charAt(w);

                if (c == '0') {
                    food.add(coords);
                }

                if (c >= 'A' && c <= 'Z') {
                    String s = String.valueOf(c);
                    SnakeDto snake = new TestSnakeDto(ToApiVersion.V1);
                    snake.setId(s);
                    snake.setName("Snake " + s);
                    snake.setHealth(healts.getOrDefault(s, 99));
                    snake.setLatency(latencies.getOrDefault(s, 100));
                    snake.setHead(coords);
                    snake.setSquad("Test squad " + s);
                    snake.setShout("Test snake " + s);
                    snake.setBody(new LinkedList<>());

                    char lowercaseC = (char) (c + 'a' - 'A');

                    for (CoordsDto current = snake.getHead(); current != null; current = getNextSnakeCoordsDtoOrNull(
                            rows, height, width, snake.getBody(), current, lowercaseC)) {
                        snake.getBody().add(current);

                        // emergency stop if looped somewhere
                        if (snake.getBody().size() > height * width) {
                            throw new IllegalStateException(
                                    "Loops within snake [" + c + "]: [" + snake.getBody() + ']');
                        }
                    }

                    // fill in snake up to start size, simulate starting conditions
                    for (int i = snake.getBody().size(); i < startSnakeSize; ++i) {
                        snake.getBody().add(snake.getBody().get(i - 1));
                    }

                    snake.setLength(snake.getBody().size());
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
