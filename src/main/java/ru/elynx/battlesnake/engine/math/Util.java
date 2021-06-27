package ru.elynx.battlesnake.engine.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
    public double scale(double from, double value, double max, double to) {
        double clamped = clamp(0.0d, value / max, 1.0d);
        return scale(from, clamped, to);
    }

    public double clamp(double min, double value, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public double scale(double from, double proportion, double to) {
        return from + (to - from) * proportion;
    }
}
