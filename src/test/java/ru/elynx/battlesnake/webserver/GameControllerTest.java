package ru.elynx.battlesnake.webserver;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.Move;

class MySnake implements IGameStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("AuThOr", "#112233", "hed", "tell", "qwerty");
    }

    @Override
    public Void processStart(GameStatePredictor gameState) {
        return null;
    }

    @Override
    public Move processMove(GameStatePredictor gameState) {
        return new Move("UP", "shshshshsh");
    }

    @Override
    public Void processEnd(GameStatePredictor gameState) {
        return null;
    }
}

@TestConfiguration
class MySnakeSupplier {
    @Bean("My Snake")
    public Supplier<IGameStrategy> makeMySnake() {
        return MySnake::new;
    }
}

@SpringBootTest
@AutoConfigureMockMvc
@Import(MySnakeSupplier.class)
@Tag("API")
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
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

    private final static String API_ENDPOINT_BASE = "/battlesnake/api/v1/snakes/My Snake";

    @Test
    void startIsOk() throws Exception {
        mockMvc.perform(post(API_ENDPOINT_BASE + "/start").content(API_EXAMPLE_GAME_STATE)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void moveHasMove() throws Exception {
        mockMvc.perform(post(API_ENDPOINT_BASE + "/move").content(API_EXAMPLE_GAME_STATE)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(containsString("move")));
    }

    @Test
    void endIsOk() throws Exception {
        mockMvc.perform(post(API_ENDPOINT_BASE + "/end").content(API_EXAMPLE_GAME_STATE)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void invalidInputNotOk() throws Exception {
        List<String> urls = new LinkedList<>();
        urls.add(API_ENDPOINT_BASE + "/start");
        urls.add(API_ENDPOINT_BASE + "/move");
        urls.add(API_ENDPOINT_BASE + "/end");

        List<String> contents = new LinkedList<>();
        contents.add("No carrier");
        contents.add("{ No carrier }");
        contents.add("{ \"no\" : \"carrier\" }");

        for (String url : urls) {
            for (String content : contents) {
                mockMvc.perform(post(url).content(content).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Test
    void invalidNameNotFound() throws Exception {
        List<String> urls = new LinkedList<>();
        urls.add(API_ENDPOINT_BASE + " 123/start");
        urls.add(API_ENDPOINT_BASE + " 123/move");
        urls.add(API_ENDPOINT_BASE + " 123/end");

        String callToMySnake123 = API_EXAMPLE_GAME_STATE.replaceAll("My Snake", "My Snake 123");

        for (String url : urls) {
            mockMvc.perform(post(url).content(callToMySnake123).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        mockMvc.perform(get(API_ENDPOINT_BASE + " 123")).andExpect(status().isNotFound());
    }
}
