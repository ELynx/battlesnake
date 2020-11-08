package ru.elynx.battlesnake.engine;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.BoardDto;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.testspecific.TestMoveV0;
import ru.elynx.battlesnake.testspecific.TestSnakeDto;

import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.elynx.battlesnake.engine.GameStrategyBasicTest.STRATEGY_NAMES;

@SpringBootTest
public class GameStrategyCaseTestV0 {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void avoidFruitSurroundedBySnake(String name) throws Exception {
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

        turn113.setYou(new TestSnakeDto(TestSnakeDto.ApiVersionTranslation.V0_TO_V1));
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

        Void nothing = gameStrategy.processStart(turn113);

        TestMoveV0 moveMaxHealth = new TestMoveV0(gameStrategy.processMove(turn113));

        assertFalse("up".equalsIgnoreCase(moveMaxHealth.getMove()));

        turn113.getYou().setHealth(0);

        TestMoveV0 moveMinHealth = new TestMoveV0(gameStrategy.processMove(turn113));

        assertFalse("up".equalsIgnoreCase(moveMinHealth.getMove()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void emptySpaceBetterThanSnake(String name) throws Exception {
        // https://play.battlesnake.com/g/646c44cd-c6f0-4a3f-ba7e-55357d0303cb/
        // wrong decision at turn 49

        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameStateDto turn49 = new GameStateDto();

        turn49.setGame(new GameDto());
        turn49.getGame().setId("646c44cd-c6f0-4a3f-ba7e-55357d0303cb");

        turn49.setTurn(49);

        turn49.setBoard(new BoardDto());
        turn49.getBoard().setWidth(11);
        turn49.getBoard().setHeight(11);

        turn49.getBoard().setFood(Collections.emptyList());
        turn49.getBoard().setHazards(Collections.emptyList());

        turn49.setYou(new TestSnakeDto(TestSnakeDto.ApiVersionTranslation.V0_TO_V1));
        turn49.getYou().setId("qwerty");
        turn49.getYou().setName("qwerty");
        turn49.getYou().setHealth(100);
        turn49.getYou().setBody(new LinkedList<>());
        turn49.getYou().getBody().add(new CoordsDto(7, 2));
        turn49.getYou().getBody().add(new CoordsDto(7, 1));
        turn49.getYou().getBody().add(new CoordsDto(8, 1));
        turn49.getYou().getBody().add(new CoordsDto(9, 1));
        turn49.getYou().getBody().add(new CoordsDto(9, 2));
        turn49.getYou().getBody().add(new CoordsDto(9, 3));
        turn49.getYou().getBody().add(new CoordsDto(8, 3));
        turn49.getYou().setShout("qwerty");

        turn49.getBoard().setSnakes(new LinkedList<>());
        turn49.getBoard().getSnakes().add(turn49.getYou());

        turn49.getBoard().getSnakes().add(new TestSnakeDto(TestSnakeDto.ApiVersionTranslation.V0_TO_V1));
        turn49.getBoard().getSnakes().get(1).setId("enemy 1");
        turn49.getBoard().getSnakes().get(1).setName("enemy 1");
        turn49.getBoard().getSnakes().get(1).setHealth(100);
        turn49.getBoard().getSnakes().get(1).setBody(new LinkedList<>());
        turn49.getBoard().getSnakes().get(1).getBody().add(new CoordsDto(3, 0));
        turn49.getBoard().getSnakes().get(1).getBody().add(new CoordsDto(3, 1));
        turn49.getBoard().getSnakes().get(1).getBody().add(new CoordsDto(4, 1));
        turn49.getBoard().getSnakes().get(1).getBody().add(new CoordsDto(5, 1));
        turn49.getBoard().getSnakes().get(1).setShout("enemy 1");

        turn49.getBoard().getSnakes().add(new TestSnakeDto(TestSnakeDto.ApiVersionTranslation.V0_TO_V1));
        turn49.getBoard().getSnakes().get(2).setId("enemy 2");
        turn49.getBoard().getSnakes().get(2).setName("enemy 2");
        turn49.getBoard().getSnakes().get(2).setHealth(100);
        turn49.getBoard().getSnakes().get(2).setBody(new LinkedList<>());
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(5, 2));
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(6, 2));
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(6, 3));
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(6, 4));
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(5, 4));
        turn49.getBoard().getSnakes().get(2).getBody().add(new CoordsDto(4, 4));
        turn49.getBoard().getSnakes().get(2).setShout("enemy 2");

        Void nothing = gameStrategy.processStart(turn49);

        TestMoveV0 moveMaxHealth = new TestMoveV0(gameStrategy.processMove(turn49));

        assertTrue("down".equalsIgnoreCase(moveMaxHealth.getMove()));

        turn49.getYou().setHealth(0);

        TestMoveV0 moveMinHealth = new TestMoveV0(gameStrategy.processMove(turn49));

        assertTrue("down".equalsIgnoreCase(moveMinHealth.getMove()));
    }
}
