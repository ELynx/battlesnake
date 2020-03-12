package ru.elynx.battlesnake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

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

        assert (gameState != null);

        assert (gameState.getGame().getId().equals("game-id-string"));

        assert (gameState.getTurn().equals(4));

        assert (gameState.getBoard().getHeight().equals(15));
        assert (gameState.getBoard().getWidth().equals(15));
        assert (gameState.getBoard().getFood().size() == 1);
        assert (gameState.getBoard().getFood().get(0).getX().equals(1));
        assert (gameState.getBoard().getFood().get(0).getY().equals(3));

        assert (gameState.getBoard().getSnakes().size() == 1);
        assert (gameState.getBoard().getSnakes().get(0).getId().equals("snake-id-string"));
        assert (gameState.getBoard().getSnakes().get(0).getName().equals("Sneky Snek"));
        assert (gameState.getBoard().getSnakes().get(0).getHealth().equals(90));
        assert (gameState.getBoard().getSnakes().get(0).getBody().size() == 1);
        assert (gameState.getBoard().getSnakes().get(0).getBody().get(0).getX().equals(1));
        assert (gameState.getBoard().getSnakes().get(0).getBody().get(0).getY().equals(3));
        assert (gameState.getBoard().getSnakes().get(0).getShout().equals("Hello my name is Sneky Snek"));

        assert (gameState.getYou().getId().equals("snake-id-string"));
        assert (gameState.getYou().getName().equals("Sneky Snek"));
        assert (gameState.getYou().getHealth().equals(90));
        assert (gameState.getYou().getBody().size() == 1);
        assert (gameState.getYou().getBody().get(0).getX().equals(1));
        assert (gameState.getYou().getBody().get(0).getY().equals(3));
        assert (gameState.getYou().getShout().equals("Hello my name is Sneky Snek"));
    }

    @Test
    public void serializeSnakeConfig() throws Exception {
        String serialized = mapper.writeValueAsString(new SnakeConfig("#dedbff", "begin", "end"));

        assert (serialized.matches(".*\\\"color\\\"\\s*\\:\\s*\\\"\\#dedbff\\\".*"));
        assert (serialized.matches(".*\\\"headType\\\"\\s*\\:\\s*\\\"begin\\\".*"));
        assert (serialized.matches(".*\\\"tailType\\\"\\s*\\:\\s*\\\"end\\\".*"));
    }

    @Test
    public void serializeMove() throws Exception {
        String serialized = mapper.writeValueAsString(new Move("down", "shshshout"));

        assert (serialized.matches(".*\\\"move\\\"\\s*\\:\\s*\\\"down\\\".*"));
        assert (serialized.matches(".*\\\"shout\\\"\\s*\\:\\s*\\\"shshshout\\\".*"));

        serialized = mapper.writeValueAsString(new Move("right"));

        assert (serialized.matches(".*\\\"move\\\"\\s*\\:\\s*\\\"right\\\".*"));
        assert (!serialized.matches(".*\\\"shout\\\".*"));
    }
}
