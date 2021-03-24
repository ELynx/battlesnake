package ru.elynx.battlesnake.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static ru.elynx.battlesnake.engine.GameStrategyBasicTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Collections;
import java.util.LinkedList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.BoardDto;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.testspecific.TestMove;
import ru.elynx.battlesnake.testspecific.TestSnakeDto;
import ru.elynx.battlesnake.testspecific.ToApiVersion;

@SpringBootTest
class GameStrategyCaseV0Test {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void avoidFruitSurroundedBySnake(String name) {
        // https://play.battlesnake.com/g/01a12be5-d44a-4d23-a073-8757fcab9db2/
        // wrong decision at turn 113

        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameStateDto turn113 = new GameStateDto();

        turn113.setGame(new GameDto());
        turn113.getGame().setId("01a12be5-d44a-4d23-a073-8757fcab9db2");

        turn113.setTurn(113);

        turn113.setBoard(new BoardDto());
        turn113.getBoard().setWidth(11);
        turn113.getBoard().setHeight(11);

        turn113.getBoard().setFood(new LinkedList<>());
        turn113.getBoard().getFood().add(new CoordsDto(1, 1));
        turn113.getBoard().setHazards(Collections.emptyList());

        turn113.setYou(new TestSnakeDto(ToApiVersion.V0));
        turn113.getYou().setId("qwerty");
        turn113.getYou().setName("qwerty");
        turn113.getYou().setHealth(100);
        turn113.getYou().setBody(new LinkedList<>());
        turn113.getYou().getBody().add(new CoordsDto(1, 2));
        turn113.getYou().getBody().add(new CoordsDto(0, 2));
        turn113.getYou().getBody().add(new CoordsDto(0, 1));
        turn113.getYou().getBody().add(new CoordsDto(0, 0));
        turn113.getYou().getBody().add(new CoordsDto(1, 0));
        turn113.getYou().getBody().add(new CoordsDto(2, 0));
        turn113.getYou().getBody().add(new CoordsDto(2, 1));
        turn113.getYou().getBody().add(new CoordsDto(3, 1));
        turn113.getYou().getBody().add(new CoordsDto(3, 0));
        turn113.getYou().getBody().add(new CoordsDto(4, 0));
        turn113.getYou().getBody().add(new CoordsDto(4, 1));
        turn113.getYou().getBody().add(new CoordsDto(4, 2));
        turn113.getYou().setShout("qwerty");

        turn113.getBoard().setSnakes(new LinkedList<>());
        turn113.getBoard().getSnakes().add(turn113.getYou());

        gameStrategy.processStart(turn113);

        TestMove moveMaxHealth = new TestMove(gameStrategy.processMove(turn113), ToApiVersion.V0);

        assertFalse(UP.equalsIgnoreCase(moveMaxHealth.getMove()));

        turn113.getYou().setHealth(0);

        TestMove moveMinHealth = new TestMove(gameStrategy.processMove(turn113), ToApiVersion.V0);

        assertFalse(UP.equalsIgnoreCase(moveMinHealth.getMove()));
    }
}
