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
            "    \"id\": \"game-00fe20da-94ad-11ea-bb37\",\n" +
            "    \"ruleset\": {\n" +
            "      \"name\": \"standard\",\n" +
            "      \"version\": \"v.1.2.3\"\n" +
            "    },\n" +
            "    \"timeout\": 500\n" +
            "  },\n" +
            "  \"turn\": 14,\n" +
            "  \"board\": {\n" +
            "    \"height\": 11,\n" +
            "    \"width\": 11,\n" +
            "    \"food\": [\n" +
            "      {\"x\": 5, \"y\": 5}, \n" +
            "      {\"x\": 9, \"y\": 0}, \n" +
            "      {\"x\": 2, \"y\": 6}\n" +
            "    ],\n" +
            "    \"hazards\": [\n" +
            "      {\"x\": 0, \"y\": 0}\n" +
            "    ],\n" +
            "    \"snakes\": [\n" +
            "      {\n" +
            "        \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n" +
            "        \"name\": \"My Snake\",\n" +
            "        \"health\": 54,\n" +
            "        \"body\": [\n" +
            "          {\"x\": 0, \"y\": 0}, \n" +
            "          {\"x\": 1, \"y\": 0}, \n" +
            "          {\"x\": 2, \"y\": 0}\n" +
            "        ],\n" +
            "        \"latency\": \"111\",\n" +
            "        \"head\": {\"x\": 0, \"y\": 0},\n" +
            "        \"length\": 3,\n" +
            "        \"shout\": \"why are we shouting??\",\n" +
            "        \"squad\": \"\"\n" +
            "      }, \n" +
            "      {\n" +
            "        \"id\": \"snake-b67f4906-94ae-11ea-bb37\",\n" +
            "        \"name\": \"Another Snake\",\n" +
            "        \"health\": 16,\n" +
            "        \"body\": [\n" +
            "          {\"x\": 5, \"y\": 4}, \n" +
            "          {\"x\": 5, \"y\": 3}, \n" +
            "          {\"x\": 6, \"y\": 3},\n" +
            "          {\"x\": 6, \"y\": 2}\n" +
            "        ],\n" +
            "        \"latency\": \"222\",\n" +
            "        \"head\": {\"x\": 5, \"y\": 4},\n" +
            "        \"length\": 4,\n" +
            "        \"shout\": \"I'm not really sure...\",\n" +
            "        \"squad\": \"THIS WAS NOT IN EXAMPLE\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"you\": {\n" +
            "    \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n" +
            "    \"name\": \"My Snake\",\n" +
            "    \"health\": 54,\n" +
            "    \"body\": [\n" +
            "      {\"x\": 0, \"y\": 0}, \n" +
            "      {\"x\": 1, \"y\": 0}, \n" +
            "      {\"x\": 2, \"y\": 0}\n" +
            "    ],\n" +
            "    \"latency\": \"111\",\n" +
            "    \"head\": {\"x\": 0, \"y\": 0},\n" +
            "    \"length\": 3,\n" +
            "    \"shout\": \"why are we shouting??\",\n" +
            "    \"squad\": \"\"\n" +
            "  }\n" +
            "}";

    @Test
    public void deserializeApiExampleGameState() throws Exception {
        GameStateDto gameState = mapper.readValue(ApiExampleGameState, GameStateDto.class);

        assertNotNull(gameState);

        assertEquals("game-00fe20da-94ad-11ea-bb37", gameState.getGame().getId());

        assertEquals("standard", gameState.getGame().getRuleset().getName());
        assertEquals("v.1.2.3", gameState.getGame().getRuleset().getVersion());

        assertEquals(500, gameState.getGame().getTimeout());

        assertEquals(14, gameState.getTurn());

        assertEquals(11, gameState.getBoard().getHeight());
        assertEquals(11, gameState.getBoard().getWidth());
        assertEquals(3, gameState.getBoard().getFood().size());
        assertEquals(5, gameState.getBoard().getFood().get(0).getX());
        assertEquals(5, gameState.getBoard().getFood().get(0).getY());
        assertEquals(9, gameState.getBoard().getFood().get(1).getX());
        assertEquals(0, gameState.getBoard().getFood().get(1).getY());
        assertEquals(2, gameState.getBoard().getFood().get(2).getX());
        assertEquals(6, gameState.getBoard().getFood().get(2).getY());

        assertEquals(1, gameState.getBoard().getHazards().size());
        assertEquals(0, gameState.getBoard().getFood().get(0).getX());
        assertEquals(0, gameState.getBoard().getFood().get(0).getY());

        assertEquals(2, gameState.getBoard().getSnakes().size());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", gameState.getBoard().getSnakes().get(0).getId());
        assertEquals("My Snake", gameState.getBoard().getSnakes().get(0).getName());
        assertEquals(54, gameState.getBoard().getSnakes().get(0).getHealth());
        assertEquals(3, gameState.getBoard().getSnakes().get(0).getBody().size());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getBody().get(0).getX());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getBody().get(0).getY());
        assertEquals(1, gameState.getBoard().getSnakes().get(0).getBody().get(1).getX());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getBody().get(1).getY());
        assertEquals(2, gameState.getBoard().getSnakes().get(0).getBody().get(2).getX());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getBody().get(2).getY());
        assertEquals(111, gameState.getBoard().getSnakes().get(0).getLatency());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getHead().getX());
        assertEquals(0, gameState.getBoard().getSnakes().get(0).getHead().getY());
        assertEquals(3, gameState.getBoard().getSnakes().get(0).getLength());
        assertEquals("why are we shouting??", gameState.getBoard().getSnakes().get(0).getShout());
        assertEquals("", gameState.getBoard().getSnakes().get(0).getSquad());

        assertEquals("snake-b67f4906-94ae-11ea-bb37", gameState.getBoard().getSnakes().get(1).getId());
        assertEquals("Another Snake", gameState.getBoard().getSnakes().get(1).getName());
        assertEquals(16, gameState.getBoard().getSnakes().get(1).getHealth());
        assertEquals(4, gameState.getBoard().getSnakes().get(1).getBody().size());
        assertEquals(5, gameState.getBoard().getSnakes().get(1).getBody().get(0).getX());
        assertEquals(4, gameState.getBoard().getSnakes().get(1).getBody().get(0).getY());
        assertEquals(5, gameState.getBoard().getSnakes().get(1).getBody().get(1).getX());
        assertEquals(3, gameState.getBoard().getSnakes().get(1).getBody().get(1).getY());
        assertEquals(6, gameState.getBoard().getSnakes().get(1).getBody().get(2).getX());
        assertEquals(4, gameState.getBoard().getSnakes().get(1).getBody().get(2).getY());
        assertEquals(6, gameState.getBoard().getSnakes().get(1).getBody().get(3).getX());
        assertEquals(2, gameState.getBoard().getSnakes().get(1).getBody().get(3).getY());
        assertEquals(222, gameState.getBoard().getSnakes().get(1).getLatency());
        assertEquals(5, gameState.getBoard().getSnakes().get(1).getHead().getX());
        assertEquals(4, gameState.getBoard().getSnakes().get(1).getHead().getY());
        assertEquals(4, gameState.getBoard().getSnakes().get(1).getLength());
        assertEquals("I'm not really sure...", gameState.getBoard().getSnakes().get(1).getShout());
        assertEquals("THIS WAS NOT IN EXAMPLE", gameState.getBoard().getSnakes().get(1).getSquad());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", gameState.getYou().getId());
        assertEquals("My Snake", gameState.getYou().getName());
        assertEquals(54, gameState.getYou().getHealth());
        assertEquals(3, gameState.getYou().getBody().size());
        assertEquals(0, gameState.getYou().getBody().get(0).getX());
        assertEquals(0, gameState.getYou().getBody().get(0).getY());
        assertEquals(1, gameState.getYou().getBody().get(1).getX());
        assertEquals(0, gameState.getYou().getBody().get(1).getY());
        assertEquals(2, gameState.getYou().getBody().get(2).getX());
        assertEquals(0, gameState.getYou().getBody().get(2).getY());
        assertEquals(111, gameState.getYou().getLatency());
        assertEquals(0, gameState.getYou().getHead().getX());
        assertEquals(0, gameState.getYou().getHead().getY());
        assertEquals(3, gameState.getYou().getLength());
        assertEquals("why are we shouting??", gameState.getYou().getShout());
        assertEquals("", gameState.getYou().getSquad());
    }

    @Test
    public void serializeBattlesnakeInfo() throws Exception {
        String serialized = mapper.writeValueAsString(new BattlesnakeInfoDto("AuThOr", "#dedbff", "begin", "end", "bestest"));

        assertTrue(serialized.matches(".*\"apiversion\"\\s*:\\s*\"1\".*"));
        assertTrue(serialized.matches(".*\"author\"\\s*:\\s*\"AuThOr\".*"));
        assertTrue(serialized.matches(".*\"color\"\\s*:\\s*\"#dedbff\".*"));
        assertTrue(serialized.matches(".*\"headType\"\\s*:\\s*\"begin\".*"));
        assertTrue(serialized.matches(".*\"tailType\"\\s*:\\s*\"end\".*"));
        assertTrue(serialized.matches(".*\"version\"\\s*:\\s*\"bestest\".*"));
    }

    @Test
    public void serializeMove() throws Exception {
        String serialized = mapper.writeValueAsString(new MoveDto("down", "shshshout"));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"down\".*"));
        assertTrue(serialized.matches(".*\"shout\"\\s*:\\s*\"shshshout\".*"));

        serialized = mapper.writeValueAsString(new MoveDto("right"));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"right\".*"));
        assertFalse(serialized.matches(".*\"shout\".*"));
    }
}
