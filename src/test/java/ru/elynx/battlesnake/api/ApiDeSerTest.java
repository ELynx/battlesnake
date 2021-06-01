package ru.elynx.battlesnake.api;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("API")
public class ApiDeSerTest {
    private final static String API_EXAMPLE_GAME_STATE = "{\n" + "  \"game\": {\n"
            + "    \"id\": \"game-00fe20da-94ad-11ea-bb37\",\n" + "    \"ruleset\": {\n"
            + "      \"name\": \"standard\",\n" + "      \"version\": \"v.1.2.3\"\n" + "    },\n"
            + "    \"timeout\": 500\n" + "  },\n" + "  \"turn\": 14,\n" + "  \"board\": {\n" + "    \"height\": 11,\n"
            + "    \"width\": 11,\n" + "    \"food\": [\n" + "      {\"x\": 5, \"y\": 5}, \n"
            + "      {\"x\": 9, \"y\": 0}, \n" + "      {\"x\": 2, \"y\": 6}\n" + "    ],\n" + "    \"hazards\": [\n"
            + "      {\"x\": 0, \"y\": 0}\n" + "    ],\n" + "    \"snakes\": [\n" + "      {\n"
            + "        \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n" + "        \"name\": \"My Snake\",\n"
            + "        \"health\": 54,\n" + "        \"body\": [\n" + "          {\"x\": 0, \"y\": 0}, \n"
            + "          {\"x\": 1, \"y\": 0}, \n" + "          {\"x\": 2, \"y\": 0}\n" + "        ],\n"
            + "        \"latency\": \"111\",\n" + "        \"head\": {\"x\": 0, \"y\": 0},\n"
            + "        \"length\": 3,\n" + "        \"shout\": \"why are we shouting??\",\n"
            + "        \"squad\": \"\"\n" + "      }, \n" + "      {\n"
            + "        \"id\": \"snake-b67f4906-94ae-11ea-bb37\",\n" + "        \"name\": \"Another Snake\",\n"
            + "        \"health\": 16,\n" + "        \"body\": [\n" + "          {\"x\": 5, \"y\": 4}, \n"
            + "          {\"x\": 5, \"y\": 3}, \n" + "          {\"x\": 6, \"y\": 3},\n"
            + "          {\"x\": 6, \"y\": 2}\n" + "        ],\n" + "        \"latency\": \"222\",\n"
            + "        \"head\": {\"x\": 5, \"y\": 4},\n" + "        \"length\": 4,\n"
            + "        \"shout\": \"I'm not really sure...\",\n" + "        \"squad\": \"THIS WAS NOT IN EXAMPLE\"\n"
            + "      }\n" + "    ]\n" + "  },\n" + "  \"you\": {\n" + "    \"id\": \"snake-508e96ac-94ad-11ea-bb37\",\n"
            + "    \"name\": \"My Snake\",\n" + "    \"health\": 54,\n" + "    \"body\": [\n"
            + "      {\"x\": 0, \"y\": 0}, \n" + "      {\"x\": 1, \"y\": 0}, \n" + "      {\"x\": 2, \"y\": 0}\n"
            + "    ],\n" + "    \"latency\": \"111\",\n" + "    \"head\": {\"x\": 0, \"y\": 0},\n"
            + "    \"length\": 3,\n" + "    \"shout\": \"why are we shouting??\",\n" + "    \"squad\": \"\"\n" + "  }\n"
            + "}";

    public static GameStateDto makeApiExampleGameStateDto() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(API_EXAMPLE_GAME_STATE, GameStateDto.class);
    }

    @Test
    void test_deserialize_exemplar_GameStateDto() throws Exception {
        GameStateDto gameStateDto = makeApiExampleGameStateDto();

        assertNotNull(gameStateDto);

        assertEquals("game-00fe20da-94ad-11ea-bb37", gameStateDto.getGame().getId());

        assertEquals("standard", gameStateDto.getGame().getRuleset().getName());
        assertEquals("v.1.2.3", gameStateDto.getGame().getRuleset().getVersion());

        assertEquals(500, gameStateDto.getGame().getTimeout());

        assertEquals(14, gameStateDto.getTurn());

        assertEquals(11, gameStateDto.getBoard().getHeight());
        assertEquals(11, gameStateDto.getBoard().getWidth());
        assertEquals(3, gameStateDto.getBoard().getFood().size());
        assertEquals(5, gameStateDto.getBoard().getFood().get(0).getX());
        assertEquals(5, gameStateDto.getBoard().getFood().get(0).getY());
        assertEquals(9, gameStateDto.getBoard().getFood().get(1).getX());
        assertEquals(0, gameStateDto.getBoard().getFood().get(1).getY());
        assertEquals(2, gameStateDto.getBoard().getFood().get(2).getX());
        assertEquals(6, gameStateDto.getBoard().getFood().get(2).getY());

        assertEquals(1, gameStateDto.getBoard().getHazards().size());
        assertEquals(0, gameStateDto.getBoard().getHazards().get(0).getX());
        assertEquals(0, gameStateDto.getBoard().getHazards().get(0).getY());

        assertEquals(2, gameStateDto.getBoard().getSnakes().size());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", gameStateDto.getBoard().getSnakes().get(0).getId());
        assertEquals("My Snake", gameStateDto.getBoard().getSnakes().get(0).getName());
        assertEquals(54, gameStateDto.getBoard().getSnakes().get(0).getHealth());
        assertEquals(3, gameStateDto.getBoard().getSnakes().get(0).getBody().size());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getBody().get(0).getX());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getBody().get(0).getY());
        assertEquals(1, gameStateDto.getBoard().getSnakes().get(0).getBody().get(1).getX());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getBody().get(1).getY());
        assertEquals(2, gameStateDto.getBoard().getSnakes().get(0).getBody().get(2).getX());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getBody().get(2).getY());
        assertEquals(111, gameStateDto.getBoard().getSnakes().get(0).getLatency());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getHead().getX());
        assertEquals(0, gameStateDto.getBoard().getSnakes().get(0).getHead().getY());
        assertEquals(3, gameStateDto.getBoard().getSnakes().get(0).getLength());
        assertEquals("why are we shouting??", gameStateDto.getBoard().getSnakes().get(0).getShout());
        assertEquals("", gameStateDto.getBoard().getSnakes().get(0).getSquad());

        assertEquals("snake-b67f4906-94ae-11ea-bb37", gameStateDto.getBoard().getSnakes().get(1).getId());
        assertEquals("Another Snake", gameStateDto.getBoard().getSnakes().get(1).getName());
        assertEquals(16, gameStateDto.getBoard().getSnakes().get(1).getHealth());
        assertEquals(4, gameStateDto.getBoard().getSnakes().get(1).getBody().size());
        assertEquals(5, gameStateDto.getBoard().getSnakes().get(1).getBody().get(0).getX());
        assertEquals(4, gameStateDto.getBoard().getSnakes().get(1).getBody().get(0).getY());
        assertEquals(5, gameStateDto.getBoard().getSnakes().get(1).getBody().get(1).getX());
        assertEquals(3, gameStateDto.getBoard().getSnakes().get(1).getBody().get(1).getY());
        assertEquals(6, gameStateDto.getBoard().getSnakes().get(1).getBody().get(2).getX());
        assertEquals(3, gameStateDto.getBoard().getSnakes().get(1).getBody().get(2).getY());
        assertEquals(6, gameStateDto.getBoard().getSnakes().get(1).getBody().get(3).getX());
        assertEquals(2, gameStateDto.getBoard().getSnakes().get(1).getBody().get(3).getY());
        assertEquals(222, gameStateDto.getBoard().getSnakes().get(1).getLatency());
        assertEquals(5, gameStateDto.getBoard().getSnakes().get(1).getHead().getX());
        assertEquals(4, gameStateDto.getBoard().getSnakes().get(1).getHead().getY());
        assertEquals(4, gameStateDto.getBoard().getSnakes().get(1).getLength());
        assertEquals("I'm not really sure...", gameStateDto.getBoard().getSnakes().get(1).getShout());
        assertEquals("THIS WAS NOT IN EXAMPLE", gameStateDto.getBoard().getSnakes().get(1).getSquad());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", gameStateDto.getYou().getId());
        assertEquals("My Snake", gameStateDto.getYou().getName());
        assertEquals(54, gameStateDto.getYou().getHealth());
        assertEquals(3, gameStateDto.getYou().getBody().size());
        assertEquals(0, gameStateDto.getYou().getBody().get(0).getX());
        assertEquals(0, gameStateDto.getYou().getBody().get(0).getY());
        assertEquals(1, gameStateDto.getYou().getBody().get(1).getX());
        assertEquals(0, gameStateDto.getYou().getBody().get(1).getY());
        assertEquals(2, gameStateDto.getYou().getBody().get(2).getX());
        assertEquals(0, gameStateDto.getYou().getBody().get(2).getY());
        assertEquals(111, gameStateDto.getYou().getLatency());
        assertEquals(0, gameStateDto.getYou().getHead().getX());
        assertEquals(0, gameStateDto.getYou().getHead().getY());
        assertEquals(3, gameStateDto.getYou().getLength());
        assertEquals("why are we shouting??", gameStateDto.getYou().getShout());
        assertEquals("", gameStateDto.getYou().getSquad());
    }

    @Test
    void test_serialize_BattlesnakeInfoDto() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String serialized = mapper
                .writeValueAsString(new BattlesnakeInfoDto("1", "AuThOr", "#dedbff", "begin", "end", "bestest"));

        assertTrue(serialized.matches(".*\"apiversion\"\\s*:\\s*\"1\".*"));
        assertTrue(serialized.matches(".*\"author\"\\s*:\\s*\"AuThOr\".*"));
        assertTrue(serialized.matches(".*\"color\"\\s*:\\s*\"#dedbff\".*"));
        assertTrue(serialized.matches(".*\"head\"\\s*:\\s*\"begin\".*"));
        assertTrue(serialized.matches(".*\"tail\"\\s*:\\s*\"end\".*"));
        assertTrue(serialized.matches(".*\"version\"\\s*:\\s*\"bestest\".*"));
    }

    @Test
    void test_serialize_MoveDto() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String serialized = mapper.writeValueAsString(new MoveDto("down", "shshshout"));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"down\".*"));
        assertTrue(serialized.matches(".*\"shout\"\\s*:\\s*\"shshshout\".*"));

        serialized = mapper.writeValueAsString(new MoveDto("right", null));

        assertTrue(serialized.matches(".*\"move\"\\s*:\\s*\"right\".*"));
        assertFalse(serialized.matches(".*\"shout\".*"));
    }
}
