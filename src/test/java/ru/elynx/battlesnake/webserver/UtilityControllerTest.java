package ru.elynx.battlesnake.webserver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Tag("API")
class UtilityControllerTest {
    private final static String API_ENDPOINT_BASE = "/utility";

    private MockMvc mockMvc;

    @BeforeEach
    void prepareMockMvc() {
        StatisticsTracker statisticsTracker = new StatisticsTracker();

        UtilityController utilityController = new UtilityController(statisticsTracker);

        mockMvc = MockMvcBuilders.standaloneSetup(utilityController).build();
    }

    @Test
    void test_status_is_ok() throws Exception {
        mockMvc.perform(get(API_ENDPOINT_BASE + "/status")).andExpect(status().isOk());
    }
}
