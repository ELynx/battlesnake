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
}
