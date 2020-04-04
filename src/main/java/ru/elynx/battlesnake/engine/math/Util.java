package ru.elynx.battlesnake.engine.math;

public class Util {
    public static double clamp(double min, double value, double max) {
        if (value < min)
            return min;

        if (value > max)
            return max;

        return value;
    }

    public static double scale(double from, double normalized, double to) {
        return from + (to - from) * normalized;
    }

    public static double scale(double from, double value, double max, double to) {
        double normalized = clamp(0.0, value / max, 1.0);
        return scale(from, normalized, to);
    }
}
