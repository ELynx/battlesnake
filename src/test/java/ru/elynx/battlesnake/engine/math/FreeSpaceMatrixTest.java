package ru.elynx.battlesnake.engine.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.api.CoordsDto;
import ru.elynx.battlesnake.api.GameStateDto;
import ru.elynx.battlesnake.api.SnakeDto;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;

@Tag("Internals")
class FreeSpaceMatrixTest {
    @Test
    void test_uninitialized_matrix() {
        for (int width = 1; width < 50; ++width) {
            for (int height = 1; height < 50; ++height) {
                FreeSpaceMatrix tested = FreeSpaceMatrix.uninitializedMatrix(width, height);
                assertEquals(0, tested.getFreeSpace(0, 0));
            }
        }
    }

    @Test
    void test_empty_matrix() {
        for (int width = 1; width < 50; ++width) {
            for (int height = 1; height < 50; ++height) {
                FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(width, height);
                int freeSpaceInEmptyMatrix = tested.getFreeSpace(0, 0);
                assertEquals(width * height, freeSpaceInEmptyMatrix);
            }
        }
    }

    @Test
    void test_is_free_outside() {
        int width = 11;
        int height = 11;

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(width, height);

        for (int x = -1; x <= width; ++x) {
            assertFalse(tested.isFree(x, -1), "Outside is not free");
            assertFalse(tested.isFree(x, height), "Outside is not free");
        }

        for (int y = -1; y <= height; ++y) {
            assertFalse(tested.isFree(-1, y), "Outside is not free");
            assertFalse(tested.isFree(width, y), "Outside is not free");
        }
    }

    @Test
    void test_get_space_outside() {
        int width = 11;
        int height = 11;

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(width, height);

        for (int x = -1; x <= width; ++x) {
            assertEquals(0, tested.getFreeSpace(x, -1));
            assertEquals(0, tested.getFreeSpace(x, height));
        }

        for (int y = -1; y <= height; ++y) {
            assertEquals(0, tested.getFreeSpace(-1, y));
            assertEquals(0, tested.getFreeSpace(width, y));
        }
    }

    @Test
    void test_set_occupied_outside() {
        int width = 11;
        int height = 11;

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(width, height);

        for (int x = -1; x <= width; ++x) {
            assertFalse(tested.setOccupied(x, -1), "Cannot mark outside");
            assertFalse(tested.setOccupied(x, height), "Cannot mark outside");
        }

        for (int y = -1; y <= height; ++y) {
            assertFalse(tested.setOccupied(-1, y), "Cannot mark outside");
            assertFalse(tested.setOccupied(width, y), "Cannot mark outside");
        }
    }

    // use snake bodies as flags to set occupied cells
    FreeSpaceMatrix buildFromAscii(String asciiGameState) {
        GameStateDto tmp = new AsciiToGameState(asciiGameState).setStartSnakeSize(1).build();

        FreeSpaceMatrix target = FreeSpaceMatrix.emptyMatrix(tmp.getBoard().getWidth(), tmp.getBoard().getHeight());

        for (SnakeDto snakeDto : tmp.getBoard().getSnakes()) {
            for (CoordsDto coordsDto : snakeDto.getBody()) {
                target.setOccupied(coordsDto.getX(), coordsDto.getY());
            }
        }

        return target;
    }

    @Test
    void test_get_space() {
        String asciiGameState = "" + //
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

        FreeSpaceMatrix tested = buildFromAscii(asciiGameState);
        long count1 = asciiGameState.chars().filter(c -> c == '_').count();
        long count2 = asciiGameState.chars().filter(c -> c == '!').count();
        long count3 = asciiGameState.chars().filter(c -> c == '@').count();
        long count4 = asciiGameState.chars().filter(c -> c == '#').count();
        long count5 = asciiGameState.chars().filter(c -> c == '$').count();
        long count6 = asciiGameState.chars().filter(c -> c == '&').count();
        long count7 = asciiGameState.chars().filter(c -> c == '%').count();

        assertEquals(count1, tested.getFreeSpace(0, 9));
        assertEquals(count2, tested.getFreeSpace(8, 6));
        assertEquals(count3, tested.getFreeSpace(28, 7));
        assertEquals(count4, tested.getFreeSpace(6, 2));
        assertEquals(count5, tested.getFreeSpace(1, 1));
        assertEquals(count6, tested.getFreeSpace(6, 0));
        assertEquals(count7, tested.getFreeSpace(39, 0));

        for (int x = -1; x <= 40; ++x) {
            for (int y = -1; y <= 10; ++y) {
                final boolean isFree = tested.isFree(x, y);
                final boolean hasFreeSpace = tested.getFreeSpace(x, y) > 0;

                assertEquals(hasFreeSpace, isFree, "Same report for two getters");
            }
        }

        tested.empty();

        assertEquals(40 * 10, tested.getFreeSpace(0, 0));
    }
}
