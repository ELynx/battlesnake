package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import ru.elynx.battlesnake.entity.MoveCommand;

public class MoveAssert {
    private final List<String> failingStrategies;
    private final MoveCommand moveCommand;
    private final Matcher<MoveCommand> matcher;

    private MoveAssert(MoveCommand moveCommand, Matcher<MoveCommand> matcher) {
        this.failingStrategies = new ArrayList<>();
        this.moveCommand = moveCommand;
        this.matcher = matcher;
    }

    static MoveAssert assertMove(MoveCommand moveCommand, Matcher<MoveCommand> matcher) {
        return new MoveAssert(moveCommand, matcher);
    }

    public MoveAssert failing(String name) {
        failingStrategies.add(name);
        return this;
    }

    public MoveAssert different(String name) {
        failingStrategies.add(name);
        return this;
    }

    public void validate(String name) throws AssertionError {
        if (failingStrategies.contains(name)) {
            assumeTrue(matcher.matches(moveCommand));
            assertThat(moveCommand, not(matcher));
        } else {
            assertThat(moveCommand, matcher);
        }
    }
}
