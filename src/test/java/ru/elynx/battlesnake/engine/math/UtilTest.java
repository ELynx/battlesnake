package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

public class UtilTest {
    @Test
    public void clamp() {
        assert (0.0d == Util.clamp(0.0d, -100.0d, 1.0d));
    }
}
