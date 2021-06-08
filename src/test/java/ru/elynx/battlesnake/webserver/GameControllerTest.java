package ru.elynx.battlesnake.webserver;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.elynx.battlesnake.engine.strategy.IGameStrategyFactory;
import ru.elynx.battlesnake.entity.mapping.BattlesnakeInfoMapper;
import ru.elynx.battlesnake.entity.mapping.GameStateMapper;
import ru.elynx.battlesnake.entity.mapping.MoveMapper;
import ru.elynx.battlesnake.entity.mapping.MoveValidator;
import ru.elynx.battlesnake.testbuilder.ApiExampleBuilder;
import ru.elynx.battlesnake.testbuilder.MySnakeGameStrategyFactory;

@Tag("API")
class GameControllerTest {
    private final static String API_ENDPOINT_BASE = "/battlesnake/api/v1/snakes/My Snake";

    private MockMvc mockMvc;

    @BeforeEach
    void prepareMockMvc()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        IGameStrategyFactory gameStrategyFactory = new MySnakeGameStrategyFactory();
        SnakeManager snakeManager = new SnakeManager(gameStrategyFactory);

        StatisticsTracker statisticsTracker = new StatisticsTracker();

        BattlesnakeInfoMapper battlesnakeInfoMapper = Mappers.getMapper(BattlesnakeInfoMapper.class);
        GameStateMapper gameStateMapper = Mappers.getMapper(GameStateMapper.class);

        MoveValidator moveValidator = new MoveValidator();
        MoveMapper moveMapper = Mappers.getMapperClass(MoveMapper.class).getConstructor(MoveValidator.class)
                .newInstance(moveValidator);

        GameController gameController = new GameController(snakeManager, statisticsTracker, battlesnakeInfoMapper,
                gameStateMapper, moveMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

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
    void test_end_is_ok_after_start() {
        assertDoesNotThrow(
                () -> mockMvc.perform(post(API_ENDPOINT_BASE + "/start").content(ApiExampleBuilder.gameState())
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()));

        assertDoesNotThrow(() -> mockMvc.perform(post(API_ENDPOINT_BASE + "/end").content(ApiExampleBuilder.gameState())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()));
    }

    @Test
    void test_end_returns_not_found_on_empty_context() {
        assertDoesNotThrow(() -> mockMvc.perform(post(API_ENDPOINT_BASE + "/end").content(ApiExampleBuilder.gameState())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()));
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
