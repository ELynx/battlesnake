package ru.elynx.battlesnake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
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
    public void startHasColor() throws Exception {
        mockMvc.perform(post("/start").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("color")));
    }

    @Test
    public void moveHasMove() throws Exception {
        mockMvc.perform(post("/move").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("move")));
    }

    @Test
    public void endIsOk() throws Exception {
        mockMvc.perform(post("/end").content(ApiExampleGameState).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void pingIsOk() throws Exception {
        mockMvc.perform(post("/ping"))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidInputNotOk() throws Exception {
        List<String> urls = new LinkedList<>();
        urls.add("/start");
        urls.add("/move");
        urls.add("/end");

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
}
