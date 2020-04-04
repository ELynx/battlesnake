package ru.elynx.battlesnake.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProtocolTest {
    private ObjectMapper mapper = new ObjectMapper();

    private String ApiExampleGameState = "{\n" +
            "  \"game\": {\n" +
            "    \"id\": \"game-id-string\"\n" +
            "  },\n" +
            "  \"turn\": 4,\n" +
            "  \"board\": {\n" +
            "    \"height\": 15,\n" +
            "    \"width\": 15,\n" +
            "    \"food\": [\n" +
            "      {\n" +
            "        \"x\": 1,\n" +
            "        \"y\": 3\n" +
            "      }\n" +
            "    ],\n" +
            "    \"snakes\": [\n" +
            "      {\n" +
            "        \"id\": \"snake-id-string\",\n" +
            "        \"name\": \"Sneky Snek\",\n" +
            "        \"health\": 90,\n" +
            "        \"body\": [\n" +
            "          {\n" +
            "            \"x\": 1,\n" +
            "            \"y\": 3\n" +
            "          }\n" +
            "        ],\n" +
            "        \"shout\": \"Hello my name is Sneky Snek\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"you\": {\n" +
            "    \"id\": \"snake-id-string\",\n" +
            "    \"name\": \"Sneky Snek\",\n" +
            "    \"health\": 90,\n" +
            "    \"body\": [\n" +
            "      {\n" +
            "        \"x\": 1,\n" +
            "        \"y\": 3\n" +
            "      }\n" +
            "    ],\n" +
            "    \"shout\": \"Hello my name is Sneky Snek\"\n" +
            "  }\n" +
            "}";

    @Test
    public void deserializeApiExampleGameState() throws Exception {
        GameState gameState = mapper.readValue(ApiExampleGameState, GameState.class);

        assertNotNull(gameState);

        assertEquals("game-id-string", gameState.getGame().getId());

        assertEquals(4, gameState.getTurn());

        assertEquals(15, gameState.getBoard().getHeight());
        assertEquals(15, gameState.getBoard().getWidth());
        assertEquals(1, gameState.getBoard().getFood().size());
        assertEquals(1, gameState.getBoard().getFood().get(0).getX());
        assertEquals(3, gameState.getBoard().getFood().get(0).getY());

        assertEquals(1, gameState.getBoard().getSnakes().size());
        assertEquals("snake-id-string", gameState.getBoard().getSnakes().get(0).getId());
        assertEquals("Sneky Snek", gameState.getBoard().getSnakes().get(0).getName());
        assertEquals(90, gameState.getBoard().getSnakes().get(0).getHealth());
        assertEquals(1, gameState.getBoard().getSnakes().get(0).getBody().size());
        assertEquals(1, gameState.getBoard().getSnakes().get(0).getBody().get(0).getX());
        assertEquals(3, gameState.getBoard().getSnakes().get(0).getBody().get(0).getY());
        assertEquals("Hello my name is Sneky Snek", gameState.getBoard().getSnakes().get(0).getShout());

        assertEquals("snake-id-string", gameState.getYou().getId());
        assertEquals("Sneky Snek", gameState.getYou().getName());
        assertEquals(90, gameState.getYou().getHealth());
        assertEquals(1, gameState.getYou().getBody().size());
        assertEquals(1, gameState.getYou().getBody().get(0).getX());
        assertEquals(3, gameState.getYou().getBody().get(0).getY());
        assertEquals("Hello my name is Sneky Snek", gameState.getYou().getShout());
    }

    @Test
    public void serializeSnakeConfig() throws Exception {
        String serialized = mapper.writeValueAsString(new SnakeConfig("#dedbff", "begin", "end"));

        assertTrue(serialized.matches(".*\"color\"\\s*:\\s*\"#dedbff\".*"));
        assertTrue(serialized.matches(".*\"headType\"\\s*:\\s*\"begin\".*"));
        assertTrue(serialized.matches(".*\"tailType\"\\s*:\\s*\"end\".*"));
    }

    @Test
    public void serializeMove() throws Exception {
        String serialized = mapper.writeValueAsString(new Move("down", "shshshout"));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"down\".*"));
        assertTrue(serialized.matches(".*\"shout\"\\s*:\\s*\"shshshout\".*"));

        serialized = mapper.writeValueAsString(new Move("right"));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"right\".*"));
        assertFalse(serialized.matches(".*\"shout\".*"));
    }

    @Test
    public void gameStateTerseIdentifier() throws Exception {
        GameState gameState = mapper.readValue(ApiExampleGameState, GameState.class);

        assertNotNull(gameState);
        assertDoesNotThrow(gameState::terseIdentification);

        //String humanReadable = gameState.terseIdentification();

        gameState.getGame().setId(null);
        assertDoesNotThrow(gameState::terseIdentification);

        gameState.setGame(null);
        assertDoesNotThrow(gameState::terseIdentification);

        gameState.setTurn(null);
        assertDoesNotThrow(gameState::terseIdentification);

        gameState.setBoard(null);
        assertDoesNotThrow(gameState::terseIdentification);

        gameState.getYou().setName(null);
        assertDoesNotThrow(gameState::terseIdentification);

        gameState.setYou(null);
        assertDoesNotThrow(gameState::terseIdentification);
    }
}
