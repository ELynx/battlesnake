package ru.elynx.battlesnake.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;

import java.util.LinkedList;

@SpringBootTest
public class GameEngineBasicTest {
    static GameState dummyGameState;
    @Autowired
    IGameEngineFactory gameEngineFactory;

    @BeforeAll
    static void fillDummies() {
        dummyGameState = new GameState();

        dummyGameState.setGame(new Game());
        dummyGameState.getGame().setId(GameEngineBasicTest.class.getSimpleName());

        dummyGameState.setBoard(new Board());
        dummyGameState.getBoard().setHeight(11);
        dummyGameState.getBoard().setWidth(15);
    }

    @BeforeEach
    void resetDummies() {
        dummyGameState.setTurn(0);

        dummyGameState.getBoard().setFood(new LinkedList<>());
        dummyGameState.getBoard().setSnakes(new LinkedList<>());

        dummyGameState.setYou(new Snake());
        dummyGameState.getYou().setId("TestYou-id");
        dummyGameState.getYou().setName("TestYou-name");
        dummyGameState.getYou().setHealth(100);
        dummyGameState.getYou().setBody(new LinkedList<>());
        dummyGameState.getYou().getBody().add(new Coords(0, 0));
        dummyGameState.getYou().setShout("TestYou-shout");

        dummyGameState.getBoard().getSnakes().add(dummyGameState.getYou());
    }

    @Test
    public void factoryMakesGameEngine() throws Exception {
        assert (gameEngineFactory != null);
        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();
        assert (gameEngine != null);
    }

    @Test
    public void gameEngineGivesConfig() throws Exception {
        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();
        SnakeConfig snakeConfig = gameEngine.processStart(dummyGameState);
        assert (snakeConfig != null);
    }

    @Test
    public void gameEngineGivesMove() throws Exception {
        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();
        Move move = gameEngine.processMove(dummyGameState);
        assert (move != null);
    }

    @Test
    public void gameEngineDoesNotThrowOnEnd() throws Exception {
        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();
        gameEngine.processEnd(dummyGameState);
    }

    @Test
    public void gameEngineDoesNotGoIntoWall() throws Exception {
        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();

        dummyGameState.getYou().getBody().get(0).setY(0);

        for (int x = 0; x < dummyGameState.getBoard().getWidth(); ++x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameEngine.processMove(dummyGameState);
            assert (!"up".equalsIgnoreCase(move.getMove()));
        }

        for (int y = 0; y < dummyGameState.getBoard().getHeight(); ++y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameEngine.processMove(dummyGameState);
            assert (!"right".equalsIgnoreCase(move.getMove()));
        }

        for (int x = dummyGameState.getBoard().getWidth() - 1; x >= 0; --x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameEngine.processMove(dummyGameState);
            assert (!"down".equalsIgnoreCase(move.getMove()));
        }

        for (int y = dummyGameState.getBoard().getHeight() - 1; y >= 0 ; --y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameEngine.processMove(dummyGameState);
            assert (!"left".equalsIgnoreCase(move.getMove()));
        }
    }
}
