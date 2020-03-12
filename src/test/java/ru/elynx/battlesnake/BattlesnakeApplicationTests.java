package ru.elynx.battlesnake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BattlesnakeApplicationTests {
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
        mockMvc.perform(post("/start").content(ApiExampleGameState))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("color")));
    }

    @Test
    public void moveHasMove() throws Exception {
        mockMvc.perform(post("/move").content(ApiExampleGameState))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("move")));
    }

    @Test
    public void endIsOk() throws Exception {
        mockMvc.perform(post("/end").content(ApiExampleGameState))
                .andExpect(status().isOk());
    }

    @Test
    public void pingIsOk() throws Exception {
        mockMvc.perform(post("/ping"))
                .andExpect(status().isOk());
    }
}
