package ru.elynx.battlesnake.engine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;

import java.util.LinkedList;

@SpringBootTest
public class GameEngineCaseTest {
    @Autowired
    IGameEngineFactory gameEngineFactory;

    @Test
    public void avoidFruitSurroundedBySnake() throws Exception {
        // https://play.battlesnake.com/g/01a12be5-d44a-4d23-a073-8757fcab9db2/
        // wrong decision at turn 113

        IGameEngine gameEngine = gameEngineFactory.makeGameEngine();

        GameState turn113 = new GameState();

        turn113.setGame(new Game());
        turn113.getGame().setId("01a12be5-d44a-4d23-a073-8757fcab9db2");

        turn113.setTurn(113);

        turn113.setBoard(new Board());
        turn113.getBoard().setWidth(11);
        turn113.getBoard().setHeight(11);

        turn113.getBoard().setFood(new LinkedList<>());
        turn113.getBoard().getFood().add(new Coords(1, 1));

        turn113.setYou(new Snake());
        turn113.getYou().setId("qwerty");
        turn113.getYou().setName("qwerty");
        turn113.getYou().setHealth(100);
        turn113.getYou().setBody(new LinkedList<>());
        turn113.getYou().getBody().add(new Coords(1, 2));
        turn113.getYou().getBody().add(new Coords(0, 2));
        turn113.getYou().getBody().add(new Coords(0, 1));
        turn113.getYou().getBody().add(new Coords(0, 0));
        turn113.getYou().getBody().add(new Coords(1, 0));
        turn113.getYou().getBody().add(new Coords(2, 0));
        turn113.getYou().getBody().add(new Coords(2, 1));
        turn113.getYou().getBody().add(new Coords(3, 1));
        turn113.getYou().getBody().add(new Coords(3, 0));
        turn113.getYou().getBody().add(new Coords(4, 0));
        turn113.getYou().getBody().add(new Coords(4, 1));
        turn113.getYou().getBody().add(new Coords(4, 2));
        turn113.getYou().setShout("qwerty");

        turn113.getBoard().setSnakes(new LinkedList<>());
        turn113.getBoard().getSnakes().add(turn113.getYou());

        Move move = gameEngine.processMove(turn113);

        assert (!"up".equalsIgnoreCase(move.getMove()));
    }
}
