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

    @Test
    public void startHasColor() throws Exception {
        mockMvc.perform(post("/start"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("color")));
    }

    @Test
    public void moveHasMove() throws Exception {
        mockMvc.perform(post("/move"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("move")));
    }

    @Test
    public void endIsOk() throws Exception {
        mockMvc.perform(post("/end"))
                .andExpect(status().isOk());
    }

    @Test
    public void pingIsOk() throws Exception {
        mockMvc.perform(post("/start"))
                .andExpect(status().isOk());
    }
}
