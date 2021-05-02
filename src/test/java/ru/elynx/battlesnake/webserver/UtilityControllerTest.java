package ru.elynx.battlesnake.webserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("API")
class UtilityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final static String API_ENDPOINT_BASE = "/utility";

    @Test
    void statusIsOk() throws Exception {
        mockMvc.perform(get(API_ENDPOINT_BASE + "/status")).andExpect(status().isOk());
    }
}
