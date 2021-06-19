package ru.elynx.battlesnake.engine.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

@Tag("Internals")
class FreeSpaceMatrixTest {
    @Test
    void test_uninitialized_matrix() {
        for (int width = 1; width < 50; ++width) {
            for (int height = 1; height < 50; ++height) {
                Dimensions dimensions = new Dimensions(width, height);
                FreeSpaceMatrix tested = FreeSpaceMatrix.uninitializedMatrix(dimensions);
                assertEquals(0, tested.getFreeSpace(new Coordinates(0, 0)));
            }
        }
    }

    @Test
    void test_empty_matrix() {
        for (int width = 1; width < 50; ++width) {
            for (int height = 1; height < 50; ++height) {
                Dimensions dimensions = new Dimensions(width, height);
                FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(dimensions);
                int freeSpaceInEmptyMatrix = tested.getFreeSpace(new Coordinates(0, 0));
                assertEquals(dimensions.area(), freeSpaceInEmptyMatrix);
            }
        }
    }

    @Test
    void test_is_free_outside() {
        int width = 11;
        int height = 11;
        Dimensions dimensions = new Dimensions(width, height);

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            assertFalse(tested.isFree(new Coordinates(x, -1)), "Outside is not free");
            assertFalse(tested.isFree(new Coordinates(x, height)), "Outside is not free");
        }

        for (int y = -1; y <= height; ++y) {
            assertFalse(tested.isFree(new Coordinates(-1, y)), "Outside is not free");
            assertFalse(tested.isFree(new Coordinates(width, y)), "Outside is not free");
        }
    }

    @Test
    void test_get_space_outside() {
        int width = 11;
        int height = 11;
        Dimensions dimensions = new Dimensions(width, height);

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            assertEquals(0, tested.getFreeSpace(new Coordinates(x, -1)));
            assertEquals(0, tested.getFreeSpace(new Coordinates(x, height)));
        }

        for (int y = -1; y <= height; ++y) {
            assertEquals(0, tested.getFreeSpace(new Coordinates(-1, y)));
            assertEquals(0, tested.getFreeSpace(new Coordinates(width, y)));
        }
    }

    @Test
    void test_set_occupied_outside() {
        int width = 11;
        int height = 11;
        Dimensions dimensions = new Dimensions(width, height);

        FreeSpaceMatrix tested = FreeSpaceMatrix.emptyMatrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            assertFalse(tested.setOccupied(new Coordinates(x, -1)), "Cannot mark outside");
            assertFalse(tested.setOccupied(new Coordinates(x, height)), "Cannot mark outside");
        }

        for (int y = -1; y <= height; ++y) {
            assertFalse(tested.setOccupied(new Coordinates(-1, y)), "Cannot mark outside");
            assertFalse(tested.setOccupied(new Coordinates(width, y)), "Cannot mark outside");
        }
    }

    // use snake bodies as flags to set occupied cells
    FreeSpaceMatrix buildFromAscii(String asciiGameState) {
        HazardPredictor tmp1 = new AsciiToGameState(asciiGameState).setStartSnakeLength(1).build();
        GameState tmp = tmp1.getGameState();

        FreeSpaceMatrix target = FreeSpaceMatrix.emptyMatrix(tmp.getBoard().getDimensions());

        for (Snake snake : tmp.getBoard().getSnakes()) {
            for (Coordinates coordinates : snake.getBody()) {
                target.setOccupied(coordinates);
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

        assertEquals(count1, tested.getFreeSpace(new Coordinates(0, 9)));
        assertEquals(count2, tested.getFreeSpace(new Coordinates(8, 6)));
        assertEquals(count3, tested.getFreeSpace(new Coordinates(28, 7)));
        assertEquals(count4, tested.getFreeSpace(new Coordinates(6, 2)));
        assertEquals(count5, tested.getFreeSpace(new Coordinates(1, 1)));
        assertEquals(count6, tested.getFreeSpace(new Coordinates(6, 0)));
        assertEquals(count7, tested.getFreeSpace(new Coordinates(39, 0)));

        for (int x = -1; x <= 40; ++x) {
            for (int y = -1; y <= 10; ++y) {
                boolean isFree = tested.isFree(new Coordinates(x, y));
                boolean hasFreeSpace = tested.getFreeSpace(new Coordinates(x, y)) > 0;

                assertEquals(hasFreeSpace, isFree, "Same report for two getters");
            }
        }

        tested.empty();

        assertEquals(40 * 10, tested.getFreeSpace(new Coordinates(0, 0)));
    }
}
