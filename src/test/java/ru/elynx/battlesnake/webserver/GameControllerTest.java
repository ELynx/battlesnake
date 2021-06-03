package ru.elynx.battlesnake.webserver;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.elynx.battlesnake.entity.MoveCommand.UP;

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
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.testbuilder.ApiExampleBuilder;

class MySnake implements IGameStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("Test Aut|hor", "#112233", "Test He|ad", "Test Ta|il", "Test Vers|ion");
    }

    @Override
    public Void processStart(HazardPredictor gameState) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor gameState) {
        return new Move(UP, "Test Sh|out");
    }

    @Override
    public Void processEnd(HazardPredictor gameState) {
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

    private final static String API_ENDPOINT_BASE = "/battlesnake/api/v1/snakes/My Snake";

    @Test
    void test_start_is_ok() {
        assertDoesNotThrow(
                () -> mockMvc.perform(post(API_ENDPOINT_BASE + "/start").content(ApiExampleBuilder.gameState())
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()));
    }

    @Test
    void test_move_is_ok_and_has_move() {
        assertDoesNotThrow(() -> mockMvc
                .perform(post(API_ENDPOINT_BASE + "/move").content(ApiExampleBuilder.gameState())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(containsString("move"))));
    }

    @Test
    void test_end_is_ok() {
        assertDoesNotThrow(() -> mockMvc.perform(post(API_ENDPOINT_BASE + "/end").content(ApiExampleBuilder.gameState())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()));
    }

    @Test
    void test_invalid_input_returns_bad_request() {
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
                assertDoesNotThrow(
                        () -> mockMvc.perform(post(url).content(content).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest()));
            }
        }
    }

    @Test
    void test_invalid_name_returns_not_found() {
        List<String> urls = new LinkedList<>();
        urls.add(API_ENDPOINT_BASE + " 123/start");
        urls.add(API_ENDPOINT_BASE + " 123/move");
        urls.add(API_ENDPOINT_BASE + " 123/end");

        String callToMySnake123 = ApiExampleBuilder.gameState().replaceAll("My Snake", "My Snake 123");

        for (String url : urls) {
            assertDoesNotThrow(
                    () -> mockMvc.perform(post(url).content(callToMySnake123).contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isNotFound()));
        }

        assertDoesNotThrow(() -> mockMvc.perform(get(API_ENDPOINT_BASE + " 123")).andExpect(status().isNotFound()));
    }
}
