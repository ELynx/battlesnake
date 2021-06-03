package ru.elynx.battlesnake.entity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.api.ApiDeSerTest;
import ru.elynx.battlesnake.api.CoordsDto;
import ru.elynx.battlesnake.api.GameStateDto;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;

@SpringBootTest
class GameStateMapperTest {
    @Test
    void test_GameStateDto_to_GameState(@Autowired GameStateMapper tested) throws Exception {
        GameStateDto dto = ApiDeSerTest.makeApiExampleGameStateDto();

        GameState entity = tested.toEntity(dto);

        assertNotNull(entity);

        assertEquals("game-00fe20da-94ad-11ea-bb37", entity.getGameId());

        assertEquals("standard", entity.getRules().getName());
        assertEquals("v.1.2.3", entity.getRules().getVersion());
        assertEquals(500, entity.getRules().getTimeout());

        assertEquals(14, entity.getTurn());

        assertEquals(11, entity.getBoard().getDimensions().getHeight());
        assertEquals(11, entity.getBoard().getDimensions().getWidth());
        assertEquals(3, entity.getBoard().getFood().size());
        assertEquals(5, entity.getBoard().getFood().get(0).getX());
        assertEquals(5, entity.getBoard().getFood().get(0).getY());
        assertEquals(9, entity.getBoard().getFood().get(1).getX());
        assertEquals(0, entity.getBoard().getFood().get(1).getY());
        assertEquals(2, entity.getBoard().getFood().get(2).getX());
        assertEquals(6, entity.getBoard().getFood().get(2).getY());

        assertEquals(1, entity.getBoard().getHazards().size());
        assertEquals(0, entity.getBoard().getHazards().get(0).getX());
        assertEquals(0, entity.getBoard().getHazards().get(0).getY());

        assertEquals(2, entity.getBoard().getSnakes().size());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", entity.getBoard().getSnakes().get(0).getId());
        assertEquals("My Snake", entity.getBoard().getSnakes().get(0).getName());
        assertEquals(54, entity.getBoard().getSnakes().get(0).getHealth());
        assertEquals(3, entity.getBoard().getSnakes().get(0).getBody().size());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getBody().get(0).getX());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getBody().get(0).getY());
        assertEquals(1, entity.getBoard().getSnakes().get(0).getBody().get(1).getX());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getBody().get(1).getY());
        assertEquals(2, entity.getBoard().getSnakes().get(0).getBody().get(2).getX());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getBody().get(2).getY());
        assertEquals(111, entity.getBoard().getSnakes().get(0).getLatency());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getHead().getX());
        assertEquals(0, entity.getBoard().getSnakes().get(0).getHead().getY());
        assertEquals(3, entity.getBoard().getSnakes().get(0).getLength());
        assertEquals("why are we shouting??", entity.getBoard().getSnakes().get(0).getShout());
        assertEquals("", entity.getBoard().getSnakes().get(0).getSquad());

        assertEquals("snake-b67f4906-94ae-11ea-bb37", entity.getBoard().getSnakes().get(1).getId());
        assertEquals("Another Snake", entity.getBoard().getSnakes().get(1).getName());
        assertEquals(16, entity.getBoard().getSnakes().get(1).getHealth());
        assertEquals(4, entity.getBoard().getSnakes().get(1).getBody().size());
        assertEquals(5, entity.getBoard().getSnakes().get(1).getBody().get(0).getX());
        assertEquals(4, entity.getBoard().getSnakes().get(1).getBody().get(0).getY());
        assertEquals(5, entity.getBoard().getSnakes().get(1).getBody().get(1).getX());
        assertEquals(3, entity.getBoard().getSnakes().get(1).getBody().get(1).getY());
        assertEquals(6, entity.getBoard().getSnakes().get(1).getBody().get(2).getX());
        assertEquals(3, entity.getBoard().getSnakes().get(1).getBody().get(2).getY());
        assertEquals(6, entity.getBoard().getSnakes().get(1).getBody().get(3).getX());
        assertEquals(2, entity.getBoard().getSnakes().get(1).getBody().get(3).getY());
        assertEquals(222, entity.getBoard().getSnakes().get(1).getLatency());
        assertEquals(5, entity.getBoard().getSnakes().get(1).getHead().getX());
        assertEquals(4, entity.getBoard().getSnakes().get(1).getHead().getY());
        assertEquals(4, entity.getBoard().getSnakes().get(1).getLength());
        assertEquals("I'm not really sure...", entity.getBoard().getSnakes().get(1).getShout());
        assertEquals("THIS WAS NOT IN EXAMPLE", entity.getBoard().getSnakes().get(1).getSquad());

        assertEquals("snake-508e96ac-94ad-11ea-bb37", entity.getYou().getId());
        assertEquals("My Snake", entity.getYou().getName());
        assertEquals(54, entity.getYou().getHealth());
        assertEquals(3, entity.getYou().getBody().size());
        assertEquals(0, entity.getYou().getBody().get(0).getX());
        assertEquals(0, entity.getYou().getBody().get(0).getY());
        assertEquals(1, entity.getYou().getBody().get(1).getX());
        assertEquals(0, entity.getYou().getBody().get(1).getY());
        assertEquals(2, entity.getYou().getBody().get(2).getX());
        assertEquals(0, entity.getYou().getBody().get(2).getY());
        assertEquals(111, entity.getYou().getLatency());
        assertEquals(0, entity.getYou().getHead().getX());
        assertEquals(0, entity.getYou().getHead().getY());
        assertEquals(3, entity.getYou().getLength());
        assertEquals("why are we shouting??", entity.getYou().getShout());
        assertEquals("", entity.getYou().getSquad());
    }

    @Test
    void test_CoordsDto_to_Coordinates(@Autowired GameStateMapper tested) {
        for (int x = -11; x <= 11; ++x) {
            for (int y = -11; y <= 11; ++y) {
                CoordsDto dto = new CoordsDto(x, y);
                Coordinates entity = tested.toEntity(dto);

                assertEquals(dto.getX(), entity.getX());
                assertEquals(dto.getY(), entity.getY());
            }
        }
    }
}
