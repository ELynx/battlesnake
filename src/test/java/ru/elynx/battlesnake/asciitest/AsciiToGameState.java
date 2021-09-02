package ru.elynx.battlesnake.asciitest;

import java.util.*;
import java.util.function.Function;
import org.javatuples.KeyValue;
import ru.elynx.battlesnake.entity.*;
import ru.elynx.battlesnake.testbuilder.ApiExampleBuilder;

public class AsciiToGameState {
    // mandatory
    private final String ascii;
    // has some defaults, thus added by "builder" pattern
    private int turn = 42;
    private String rulesetName = ApiExampleBuilder.standardRulesetName();
    private int startSnakeLength = 3;
    private String hazards = null;
    private int hazardDamage = 15;
    // per-snakes
    private final Map<String, Integer> healths = new HashMap<>();
    private final Map<String, Integer> latencies = new HashMap<>();
    private final Map<String, Integer> lengths = new HashMap<>();

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

    public AsciiToGameState setStartSnakeLength(int startSnakeLength) {
        if (startSnakeLength <= 0)
            throw new IllegalArgumentException("Snake size must be greater than 0");

        this.startSnakeLength = startSnakeLength;
        return this;
    }

    /**
     * Set hazards field. Also set ruleset name to Royale to avoid extra step.
     *
     * @param hazards
     *            field
     * @return Builder with hazards field and ruleset name altered
     */
    public AsciiToGameState setHazards(String hazards) {
        if (ascii.length() != hazards.length())
            throw new IllegalArgumentException("Hazards must be size of main ascii field");

        this.hazards = hazards;
        return setRulesetName(ApiExampleBuilder.royaleRulesetName());
    }

    public AsciiToGameState setHazardDamage(int hazardDamage) {
        if (hazardDamage < 0)
            throw new IllegalArgumentException("Hazard Damage must be greater or equal to 0");

        this.hazardDamage = hazardDamage;
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

    public AsciiToGameState setLength(String name, int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be greater than 0");

        lengths.put(name, length);
        return this;
    }

    public GameState build() {
        var rows = extractMainRows();
        var dimensions = dimensionsFromRows(rows);

        var food = new LinkedList<Coordinates>();
        var snakes = new LinkedList<Snake>();

        Snake you = null;

        for (var w = 0; w < dimensions.getWidth(); ++w) {
            for (var h = 0; h < dimensions.getHeight(); ++h) {
                var symbol = rows.get(h).charAt(w);
                var coords = coordinatesFromWidthAndHeight(dimensions, w, h);

                if (symbol == '0') {
                    food.add(coords);
                }

                if (symbol >= 'A' && symbol <= 'Z') {
                    var snake = makeSnakeFromStart(rows, dimensions, coords, symbol);

                    snakes.add(snake);

                    if (symbol == 'Y') {
                        you = snake;
                    }
                }
            }
        }

        if (snakes.isEmpty()) {
            throw new IllegalStateException("No snakes found");
        }

        if (you == null) {
            throw new IllegalStateException("You snake not found");
        }

        var gameId = buildGameId();
        var turn = buildTurn();
        var rules = buildRules();

        var hazards = buildHazards();
        var board = new Board(dimensions, food, hazards, snakes);

        return new GameState(gameId, turn, rules, board, you);
    }

    private String buildGameId() {
        return "test-game-id";
    }

    private int buildTurn() {
        return turn;
    }

    private Rules buildRules() {
        return new Rules(rulesetName, "1.0.0", 500, hazardDamage);
    }

    private Snake makeSnakeFromStart(List<String> rows, Dimensions dimensions, Coordinates coords, char c) {
        String id = String.valueOf(c);
        String name = "Snake " + id;
        String squad = "Test squad " + id;
        String shout = "Test snake " + id;
        Coordinates head = coords;
        int health = healths.getOrDefault(id, Snake.getMaxHealth() - 1);
        int latency = latencies.getOrDefault(id, 100);

        char lowercaseC = (char) (c + 'a' - 'A');

        LinkedList<Coordinates> body = new LinkedList<>();
        for (Coordinates current = head; current != null; current = getNextSnakeCoordsDtoOrNull(rows,
                dimensions.getHeight(), dimensions.getWidth(), body, current, lowercaseC)) {
            body.add(current);

            // emergency stop if looped somewhere
            if (body.size() > dimensions.getArea()) {
                throw new IllegalStateException("Loops within snake [" + c + "]: [" + body + ']');
            }
        }

        // fill in snake up to start size, simulate starting conditions
        int snakeLength = lengths.getOrDefault(id, startSnakeLength);
        for (int i = body.size(); i < snakeLength; ++i) {
            body.add(body.get(i - 1));
        }

        int length = body.size();

        Snake snake = new Snake(id, name, health, body, latency, head, length, shout, squad);
        return snake;
    }

    private List<String> extractMainRows() {
        validateAscii();
        return extractRows(ascii);
    }

    private void validateAscii() {
        if (ascii.indexOf('V') >= 0) {
            throw new IllegalStateException("V is not allowed in ascii");
        }
    }

    private List<String> extractRows(String from) {
        var rows = extractRowsByNewline(from);
        validateRows(rows);
        return rows;
    }

    private List<String> extractRowsByNewline(String from) {
        List<String> rows = Arrays.asList(from.split("\\r?\\n"));
        rows.removeAll(Arrays.asList("", null));

        return rows;
    }

    private void validateRows(List<String> rows) {
        if (rows.size() == 0) {
            throw new IllegalStateException("Could not find rows");
        }

        int width = rows.get(0).length();
        rows.forEach(s -> {
            if (s.isEmpty() || s.length() != width) {
                throw new IllegalStateException("Rows have different or invalid size");
            }
        });
    }

    Dimensions dimensionsFromRows(List<String> rows) {
        return new Dimensions(rows.get(0).length(), rows.size());
    }

    private Coordinates getNextSnakeCoordsDtoOrNull(List<String> rows, int height, int width, List<Coordinates> soFar,
            Coordinates current, char bodyChar) {
        List<KeyValue<Coordinates, Character>> neighbours = getNeighbours(rows, height, width, soFar, current,
                bodyChar);

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

        int xLeft = x - 1;
        int xRight = x + 1;
        int yDown = y - 1;
        int yUp = y + 1;

        // arrow pointing to center
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(xLeft, y), '>'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(x, yUp), 'v'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(xRight, y), '<'));
        addIfChecksUp.apply(new KeyValue<>(new Coordinates(x, yDown), '^'));

        return result;
    }

    private List<Coordinates> buildHazards() {
        if (hazards == null) {
            return Collections.emptyList();
        }

        var rows = extractHazardRows();
        var dimensions = dimensionsFromRows(rows);

        var result = new LinkedList<Coordinates>();
        for (var w = 0; w < dimensions.getWidth(); ++w) {
            for (var h = 0; h < dimensions.getHeight(); ++h) {
                var symbol = rows.get(h).charAt(w);

                if (symbol == 'H') {
                    result.add(coordinatesFromWidthAndHeight(dimensions, w, h));
                }
            }
        }

        return result;
    }

    List<String> extractHazardRows() {
        var hazardRows = extractRows(hazards);
        var mainRows = extractRows(ascii);

        if (!dimensionsFromRows(hazardRows).equals(dimensionsFromRows(mainRows))) {
            throw new IllegalStateException("Different dimensions of ascii and hazards");
        }

        return hazardRows;
    }

    Coordinates coordinatesFromWidthAndHeight(Dimensions dimensions, int w, int h) {
        int y = dimensions.getHeight() - h - 1;
        return new Coordinates(w, y);
    }
}
