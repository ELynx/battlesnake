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
        validateAscii();

        var rows = extractRows();
        var dimensions = dimensionsFromRows(rows);

        int width = dimensions.getWidth();
        int height = dimensions.getHeight();

        LinkedList<Coordinates> food = new LinkedList<>();
        LinkedList<Snake> snakes = new LinkedList<>();
        Snake you = null;

        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                int x = w;
                int y = height - h - 1;
                Coordinates coordinates = new Coordinates(x, y);

                char c = rows.get(h).charAt(w);

                if (c == '0') {
                    food.add(coordinates);
                }

                if (c >= 'A' && c <= 'Z') {
                    String id = String.valueOf(c);
                    String name = "Snake " + id;
                    String squad = "Test squad " + id;
                    String shout = "Test snake " + id;
                    Coordinates head = coordinates;
                    int health = healths.getOrDefault(id, Snake.getMaxHealth() - 1);
                    int latency = latencies.getOrDefault(id, 100);

                    char lowercaseC = (char) (c + 'a' - 'A');

                    LinkedList<Coordinates> body = new LinkedList<>();
                    for (Coordinates current = head; current != null; current = getNextSnakeCoordsDtoOrNull(rows,
                            height, width, body, current, lowercaseC)) {
                        body.add(current);

                        // emergency stop if looped somewhere
                        if (body.size() > height * width) {
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

        List<Coordinates> generatedHazards;

        if (hazards == null) {
            generatedHazards = Collections.emptyList();
        } else {
            List<Coordinates> toAdd = new LinkedList<>();

            List<String> rows2 = Arrays.asList(hazards.split("\\r?\\n"));
            rows2.removeAll(Arrays.asList("", null));

            for (int w = 0; w < width; ++w) {
                for (int h = 0; h < height; ++h) {
                    int x = w;
                    int y = height - h - 1;
                    Coordinates coords = new Coordinates(x, y);

                    char c = rows2.get(h).charAt(w);

                    if (c == 'H') {
                        toAdd.add(coords);
                    }
                }
            }

            generatedHazards = toAdd;
        }

        Board board = new Board(dimensions, food, generatedHazards, snakes);

        Rules rules = new Rules(rulesetName, "1.0.0", 500, hazardDamage);

        return new GameState("test-game-id", turn, rules, board, you);
    }

    private void validateAscii() {
        if (ascii.indexOf('V') >= 0) {
            throw new IllegalStateException("V is not allowed in ascii");
        }
    }

    private List<String> extractRows() {
        var rows = extractRowsByNewline();
        validateRows(rows);
        return rows;
    }

    private List<String> extractRowsByNewline() {
        List<String> rows = Arrays.asList(ascii.split("\\r?\\n"));
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
}
