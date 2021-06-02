package ru.elynx.battlesnake.asciitest;

import java.util.*;
import java.util.function.Function;
import org.javatuples.KeyValue;
import ru.elynx.battlesnake.api.*;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.*;
import ru.elynx.battlesnake.testspecific.TestSnakeDto;
import ru.elynx.battlesnake.testspecific.ToApiVersion;

public class AsciiToGameState {
    // mandatory
    private final String ascii;
    // has some defaults, thus added by "builder" pattern
    private int turn = 42;
    private String rulesetName = "standard";
    private int startSnakeSize = 3;
    private String hazards = null;
    private int hazardStep = 25;
    // per-snakes
    private final Map<String, Integer> healths = new HashMap<>();
    private final Map<String, Integer> latencies = new HashMap<>();

    public AsciiToGameState(String ascii) {
        this.ascii = ascii;
    }

    public AsciiToGameState setTurn(int turn) {
        if (turn < 0)
            throw new IllegalArgumentException("Turn must be greater or equal to 0");

        this.turn = turn;
        return this;
    }

    public AsciiToGameState setRulesetName(String rulesetName) {
        this.rulesetName = rulesetName;
        return this;
    }

    public AsciiToGameState setStartSnakeSize(int startSnakeSize) {
        if (startSnakeSize <= 0)
            throw new IllegalArgumentException("Snake size must be greater than 0");

        this.startSnakeSize = startSnakeSize;
        return this;
    }

    public AsciiToGameState setHazards(String hazards) {
        if (ascii.length() != hazards.length())
            throw new IllegalArgumentException("Hazards must be size of main ascii field");

        this.hazards = hazards;
        return this;
    }

    public AsciiToGameState setHazardStep(int hazardStep) {
        if (hazardStep < 0)
            throw new IllegalArgumentException("Hazard step is greater or equal to zero");

        this.hazardStep = hazardStep;
        return this;
    }

    public AsciiToGameState setHealth(String name, int health) {
        if (health < 0)
            throw new IllegalArgumentException("Health must be greater or equal to 0");

        healths.put(name, health);
        return this;
    }

    public AsciiToGameState setLatency(String name, int latency) {
        if (latency < 0)
            throw new IllegalArgumentException("Latency must be greater or equal to 0");

        latencies.put(name, latency);
        return this;
    }

    private List<KeyValue<Coordinates, Character>> getNeighbours(List<String> rows, int height, int width,
                                                                 List<Coordinates> soFar, Coordinates center, char lookupChar) {
        LinkedList<KeyValue<Coordinates, Character>> result = new LinkedList<>();

        Function<KeyValue<Coordinates, Character>, Void> addIfChecksUp = pair -> {
            Coordinates coords = pair.getKey();

            // avoid already found pieces
            if (soFar.contains(coords)) {
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
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(xleft, y), '>'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(x, yup), 'v'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(xright, y), '<'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(x, ydown), '^'));

        return result;
    }

    private Coordinates getNextSnakeCoordsDtoOrNull(List<String> rows, int height, int width, List<Coordinates> soFar,
                                                    Coordinates current, char bodyChar) {
        List<KeyValue<Coordinates, Character>> neighbours = getNeighbours(rows, height, width, soFar, current, bodyChar);

        // priority 1 - arrows pointing
        for (KeyValue<Coordinates, Character> keyValue : neighbours) {
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

    public HazardPredictor build() {
        if (ascii.indexOf('V') >= 0) {
            throw new IllegalStateException("V is not allowed in ascii");
        }

        Rules rules = new Rules(rulesetName, "1.0.0", 500);
        GameState game = new GameState("test-game-id", turn, rules, null, null);
        HazardPredictor gameState = new HazardPredictor(game, hazardStep);

        Board board = new Board();

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

        Dimensions dimensions = new Dimensions(width, height);

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
                    snake.setHealth(healths.getOrDefault(s, 99));
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

        if (hazards == null) {
            board.setHazards(Collections.emptyList());
        } else {
            List<CoordsDto> toAdd = new LinkedList<>();

            List<String> rows2 = Arrays.asList(hazards.split("\\r?\\n"));
            rows2.removeAll(Arrays.asList("", null));

            for (int w = 0; w < width; ++w) {
                for (int h = 0; h < height; ++h) {
                    int x = w;
                    int y = height - h - 1;
                    CoordsDto coords = new CoordsDto(x, y);

                    char c = rows2.get(h).charAt(w);

                    if (c == 'H') {
                        toAdd.add(coords);
                    }
                }
            }

            board.setHazards(toAdd);
        }

        board.setSnakes(snakes);

        gameState.setBoard(board);
        gameState.setYou(you);

        return gameState;
    }
}
