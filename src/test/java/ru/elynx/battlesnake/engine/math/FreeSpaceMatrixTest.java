package ru.elynx.battlesnake.engine.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

class FreeSpaceMatrixTest {

    @Test
    void test_creation() {
        for (int w = 1; w < 50; ++w) {
            for (int h = 1; h < 50; ++h) {
                FreeSpaceMatrix tested = FreeSpaceMatrix.emptyFreeSpaceMatrix(w, h);
                final int freeSpaceInEmptyMatrix = tested.getSpace(0, 0);
                assertEquals(w * h, freeSpaceInEmptyMatrix);
            }
        }
    }

    @Test
    void test_get_space_outside() {
        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyFreeSpaceMatrix(11, 11);
        for (int x = -1; x < 12; ++x) {
            assertEquals(0, tested.getSpace(x, -1));
            assertEquals(0, tested.getSpace(x, 12));
        }
        for (int y = -1; y < 12; ++y) {
            assertEquals(0, tested.getSpace(-1, y));
            assertEquals(0, tested.getSpace(12, y));
        }
    }

    @Test
    void test_set_occupied_outside() {
        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyFreeSpaceMatrix(11, 11);
        for (int x = -1; x < 12; ++x) {
            assertFalse(tested.setOccupied(x, -1));
            assertFalse(tested.setOccupied(x, 12));
        }
        for (int y = -1; y < 12; ++y) {
            assertFalse(tested.setOccupied(-1, y));
            assertFalse(tested.setOccupied(12, y));
        }
    }

    FreeSpaceMatrix mazeMaker(String airQuotMaze) {
        GameStateDto tmp = new AsciiToGameState(airQuotMaze).setStartSnakeSize(1).build();

        FreeSpaceMatrix target = FreeSpaceMatrix.emptyFreeSpaceMatrix(tmp.getBoard().getWidth(),
                tmp.getBoard().getHeight());

        for (SnakeDto snakeDto : tmp.getBoard().getSnakes()) {
            for (CoordsDto coordsDto : snakeDto.getBody()) {
                target.setOccupied(coordsDto.getX(), coordsDto.getY());
            }
        }

        return target;
    }

    @Test
    void test_get_space_maze() {
        String maze = "" + //
                "________________________________________\n" + //
                "____E>>>ee________________D>ddd_________\n" + //
                "____^<<!!ee_______________^@@@d_________\n" + //
                "______^!!!e_______________ddddd_________\n" + //
                "______^eeee_____________________________\n" + //
                "Yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\n" + //
                "################cccc&&&&&&&&&&&&&&&&&&&&\n" + //
                "B>bbbb#############c&&&&&&&&&&&&&&&aaaaa\n" + //
                "^$$$$bCccccccccccccc&&&&&&&&&&&&&&&a%%%%\n" + //
                "bbbbbb&&&&&&&&&&&&&&&&&&&&&&&&&&&&&A%%%%\n";

        FreeSpaceMatrix tested = mazeMaker(maze);
        final long count1 = maze.chars().filter(c -> c == '_').count();
        final long count2 = maze.chars().filter(c -> c == '!').count();
        final long count3 = maze.chars().filter(c -> c == '@').count();
        final long count4 = maze.chars().filter(c -> c == '#').count();
        final long count5 = maze.chars().filter(c -> c == '$').count();
        final long count6 = maze.chars().filter(c -> c == '&').count();
        final long count7 = maze.chars().filter(c -> c == '%').count();

        assertEquals(count1, tested.getSpace(0, 9));
        assertEquals(count2, tested.getSpace(8, 6));
        assertEquals(count3, tested.getSpace(28, 7));
        assertEquals(count4, tested.getSpace(6, 2));
        assertEquals(count5, tested.getSpace(1, 1));
        assertEquals(count6, tested.getSpace(6, 0));
        assertEquals(count7, tested.getSpace(39, 0));
    }
}