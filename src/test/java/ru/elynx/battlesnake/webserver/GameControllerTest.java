package ru.elynx.battlesnake.webserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
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

    private String ApiEndpointBase = "/battlesnake/api/v1/snake/Test  321 SnAkE";

    @Test
    public void startIsOk() throws Exception {
        mockMvc.perform(post(ApiEndpointBase + "/start").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void moveHasMove() throws Exception {
        mockMvc.perform(post(ApiEndpointBase + "/move").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("move")));
    }

    @Test
    public void endIsOk() throws Exception {
        mockMvc.perform(post(ApiEndpointBase + "/end").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidInputNotOk() throws Exception {
        List<String> urls = new LinkedList<>();
        urls.add(ApiEndpointBase + "/start");
        urls.add(ApiEndpointBase + "/move");
        urls.add(ApiEndpointBase + "/end");

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
    public void invalidNameNotFound() throws Exception {
        List<String> urls = new LinkedList<>();
        urls.add(ApiEndpointBase + "123/start");
        urls.add(ApiEndpointBase + "123/move");
        urls.add(ApiEndpointBase + "123/end");

        for (String url : urls) {
            mockMvc.perform(post(url).content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        mockMvc.perform(get(ApiEndpointBase + "123"))
                .andExpect(status().isNotFound());
    }

    private static class TestGameStrategy implements IGameStrategy {
        public TestGameStrategy() {
        }

        @Override
        public BattlesnakeInfo getBattesnakeInfo() {
            return new BattlesnakeInfo(
                    "AuthoR",
                    "#112233",
                    "hed",
                    "tell",
                    "over 9000"
            );
        }

        @Override
        public Void processStart(GameStateDto gameState) {
            return null;
        }

        @Override
        public Move processMove(GameStateDto gameState) {
            return new Move(
                    "UP", "tuohs"
            );
        }

        @Override
        public Void processEnd(GameStateDto gameState) {
            return null;
        }
    }

    @Configuration
    public static class TestStrategyConfiguration {
        @Bean("Test  321 SnAkE")
        public Supplier<IGameStrategy> testStrategy() {
            return () -> new TestGameStrategy();
        }
    }
}
