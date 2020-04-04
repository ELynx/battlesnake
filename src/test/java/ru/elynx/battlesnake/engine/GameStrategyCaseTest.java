package ru.elynx.battlesnake.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;

import java.util.LinkedList;

@SpringBootTest
public class GameStrategyCaseTest {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource("ru.elynx.battlesnake.engine.GameStrategyBasicTest#provideStrategyIndexes")
    public void avoidFruitSurroundedBySnake(Integer index) throws Exception {
        // https://play.battlesnake.com/g/01a12be5-d44a-4d23-a073-8757fcab9db2/
        // wrong decision at turn 113

        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);

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

        Move moveMaxHealth = gameStrategy.processMove(turn113);

        assert (!"up".equalsIgnoreCase(moveMaxHealth.getMove()));

        turn113.getYou().setHealth(0);

        Move moveMinHealth = gameStrategy.processMove(turn113);

        assert (!"up".equalsIgnoreCase(moveMinHealth.getMove()));
    }

    @ParameterizedTest
    @MethodSource("ru.elynx.battlesnake.engine.GameStrategyBasicTest#provideStrategyIndexes")
    public void emptySpaceBetterThanSnake(Integer index) throws Exception {
        // https://play.battlesnake.com/g/646c44cd-c6f0-4a3f-ba7e-55357d0303cb/
        // wrong decision at turn 49

        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);

        GameState turn49 = new GameState();

        turn49.setGame(new Game());
        turn49.getGame().setId("646c44cd-c6f0-4a3f-ba7e-55357d0303cb");

        turn49.setTurn(49);

        turn49.setBoard(new Board());
        turn49.getBoard().setWidth(11);
        turn49.getBoard().setHeight(11);

        turn49.getBoard().setFood(new LinkedList<>());

        turn49.setYou(new Snake());
        turn49.getYou().setId("qwerty");
        turn49.getYou().setName("qwerty");
        turn49.getYou().setHealth(100);
        turn49.getYou().setBody(new LinkedList<>());
        turn49.getYou().getBody().add(new Coords(7, 2));
        turn49.getYou().getBody().add(new Coords(7, 1));
        turn49.getYou().getBody().add(new Coords(8, 1));
        turn49.getYou().getBody().add(new Coords(9, 1));
        turn49.getYou().getBody().add(new Coords(9, 2));
        turn49.getYou().getBody().add(new Coords(9, 3));
        turn49.getYou().getBody().add(new Coords(8, 3));
        turn49.getYou().setShout("qwerty");

        turn49.getBoard().setSnakes(new LinkedList<>());
        turn49.getBoard().getSnakes().add(turn49.getYou());

        turn49.getBoard().getSnakes().add(new Snake());
        turn49.getBoard().getSnakes().get(1).setId("enemy 1");
        turn49.getBoard().getSnakes().get(1).setName("enemy 1");
        turn49.getBoard().getSnakes().get(1).setHealth(100);
        turn49.getBoard().getSnakes().get(1).setBody(new LinkedList<>());
        turn49.getBoard().getSnakes().get(1).getBody().add(new Coords(3, 0));
        turn49.getBoard().getSnakes().get(1).getBody().add(new Coords(3, 1));
        turn49.getBoard().getSnakes().get(1).getBody().add(new Coords(4, 1));
        turn49.getBoard().getSnakes().get(1).getBody().add(new Coords(5, 1));
        turn49.getBoard().getSnakes().get(1).setShout("enemy 1");

        turn49.getBoard().getSnakes().add(new Snake());
        turn49.getBoard().getSnakes().get(2).setId("enemy 2");
        turn49.getBoard().getSnakes().get(2).setName("enemy 2");
        turn49.getBoard().getSnakes().get(2).setHealth(100);
        turn49.getBoard().getSnakes().get(2).setBody(new LinkedList<>());
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(5, 2));
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(6, 2));
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(6, 3));
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(6, 4));
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(5, 4));
        turn49.getBoard().getSnakes().get(2).getBody().add(new Coords(4, 4));
        turn49.getBoard().getSnakes().get(2).setShout("enemy 2");

        Move moveMaxHealth = gameStrategy.processMove(turn49);

        assert ("down".equalsIgnoreCase(moveMaxHealth.getMove()));

        turn49.getYou().setHealth(0);

        Move moveMinHealth = gameStrategy.processMove(turn49);

        assert ("down".equalsIgnoreCase(moveMinHealth.getMove()));
    }
}
