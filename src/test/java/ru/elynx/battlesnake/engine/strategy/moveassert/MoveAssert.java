package ru.elynx.battlesnake.engine.strategy.moveassert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import ru.elynx.battlesnake.entity.MoveCommand;

public class MoveAssert {
    private final List<String> failingStrategies;
    private final List<String> differentStrategies;
    private final MoveCommand moveCommand;
    private final Matcher<MoveCommand> matcher;

    private MoveAssert(MoveCommand moveCommand, Matcher<MoveCommand> matcher) {
        this.failingStrategies = new ArrayList<>();
        this.differentStrategies = new ArrayList<>();
        this.moveCommand = moveCommand;
        this.matcher = matcher;
    }

    public static MoveAssert assertMove(MoveCommand moveCommand, Matcher<MoveCommand> matcher) {
        return new MoveAssert(moveCommand, matcher);
    }

    public MoveAssert failing(String name) {
        failingStrategies.add(name);
        return this;
    }

    public MoveAssert different(String name) {
        differentStrategies.add(name);
        return this;
    }

    public void validate(String name) throws AssertionError {
        checkInternalState();
        validateImpl(name);
    }

    private void checkInternalState() {
        for (String failing : failingStrategies) {
            if (differentStrategies.contains(failing)) {
                fail("Both `failing` and `different` applied to strategy " + failing);
            }
        }
    }

    private void validateImpl(String name) {
        if (differentStrategies.contains(name)) {
            if (matcher.matches(moveCommand)) {
                fail();
            } else {
                return;
            }
        }

        if (failingStrategies.contains(name)) {
            assumeTrue(matcher.matches(moveCommand));
            fail();
        } else {
            assertThat(moveCommand, matcher);
        }
    }
}
