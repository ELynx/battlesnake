package ru.elynx.battlesnake.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;

import java.util.LinkedList;

@SpringBootTest
public class GameStrategyBasicTest {
    static GameState dummyGameState;
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @BeforeAll
    static void fillDummies() {
        dummyGameState = new GameState();

        dummyGameState.setGame(new Game());
        dummyGameState.getGame().setId(GameStrategyBasicTest.class.getSimpleName());

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
    public void factoryMakesGameStrategy() throws Exception {
        assert (gameStrategyFactory != null);
        IGameStrategy gameStrategy = gameStrategyFactory.makeGameStrategy();
        assert (gameStrategy != null);
    }

    @Test
    public void gameStrategyGivesConfig() throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.makeGameStrategy();
        SnakeConfig snakeConfig = gameStrategy.processStart(dummyGameState);
        assert (snakeConfig != null);
    }

    @Test
    public void gameStrategyGivesMove() throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.makeGameStrategy();
        Move move = gameStrategy.processMove(dummyGameState);
        assert (move != null);
    }

    @Test
    public void gameStrategyDoesNotThrowOnEnd() throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.makeGameStrategy();
        gameStrategy.processEnd(dummyGameState);
    }

    @Test
    public void gameStrategyDoesNotGoIntoWall() throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.makeGameStrategy();

        dummyGameState.getYou().getBody().get(0).setY(0);

        for (int x = 0; x < dummyGameState.getBoard().getWidth(); ++x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameStrategy.processMove(dummyGameState);
            assert (!"up".equalsIgnoreCase(move.getMove()));
        }

        for (int y = 0; y < dummyGameState.getBoard().getHeight(); ++y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameStrategy.processMove(dummyGameState);
            assert (!"right".equalsIgnoreCase(move.getMove()));
        }

        for (int x = dummyGameState.getBoard().getWidth() - 1; x >= 0; --x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameStrategy.processMove(dummyGameState);
            assert (!"down".equalsIgnoreCase(move.getMove()));
        }

        for (int y = dummyGameState.getBoard().getHeight() - 1; y >= 0; --y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameStrategy.processMove(dummyGameState);
            assert (!"left".equalsIgnoreCase(move.getMove()));
        }
    }
}
