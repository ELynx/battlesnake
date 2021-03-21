package ru.elynx.battlesnake.asciitest;

import java.util.HashMap;
import java.util.Map;
import ru.elynx.battlesnake.protocol.GameDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.RulesetDto;

public class AsciiToGameState {
    // mandatory
    private final String ascii;
    // has some defaults, thus added by "builder" pattern
    private int turn = 0;
    // per-snakes
    private Map<String, Integer> healts = new HashMap<>();
    private Map<String, Integer> latencies = new HashMap<>();

    public AsciiToGameState(String ascii) {
        this.ascii = ascii;
    }

    AsciiToGameState setTurn(int turn) {
        this.turn = turn;
        return this;
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
        RulesetDto ruleset = new RulesetDto();
        ruleset.setName("standard");
        ruleset.setVersion("1.0.0");

        GameDto game = new GameDto();
        game.setId("test-game-id");
        game.setRuleset(ruleset);
        game.setTimeout(500);

        GameStateDto result = new GameStateDto();
        result.setGame(game);
        result.setTurn(turn);

        // TODO rest of fucking owl

        return result;
    }
}
