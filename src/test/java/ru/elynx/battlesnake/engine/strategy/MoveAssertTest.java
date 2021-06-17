package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.MoveCommand;

class MoveAssertTest {
    @Test
    void test_assert_move_valid() {
        MoveAssert tested = MoveAssert.assertMove(MoveCommand.UP, equalTo(MoveCommand.UP));
        assertDoesNotThrow(() -> tested.validate("Foo"));
    }

    @Test
    void test_assert_move_invalid() {
        MoveAssert tested = MoveAssert.assertMove(MoveCommand.UP, equalTo(MoveCommand.DOWN));
        assertThrows(AssertionError.class, () -> tested.validate("Foo"));
    }

    @Test
    void test_assert_move_failing_valid() {
        MoveAssert tested = MoveAssert.assertMove(MoveCommand.UP, equalTo(MoveCommand.DOWN)).failing("Foo");
        assertDoesNotThrow(() -> tested.validate("Foo"));
    }

    @Test
    void test_assert_move_failing_invalid() {
        MoveAssert tested = MoveAssert.assertMove(MoveCommand.UP, equalTo(MoveCommand.UP)).failing("Foo");
        assertThrows(AssertionError.class, () -> tested.validate("Foo"));
    }
}
