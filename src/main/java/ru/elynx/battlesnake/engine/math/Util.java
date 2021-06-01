package ru.elynx.battlesnake.engine.math;

public class Util {
    private Util() {
    }

    public static double scale(double from, double value, double max, double to) {
        double clamped = clamp(0.0, value / max, 1.0);
        return scale(from, clamped, to);
    }

    public static double clamp(double min, double value, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double scale(double from, double proportion, double to) {
        return from + (to - from) * proportion;
    }
}
