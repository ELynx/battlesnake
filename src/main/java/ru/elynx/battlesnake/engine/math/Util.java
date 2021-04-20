package ru.elynx.battlesnake.engine.math;

import ru.elynx.battlesnake.protocol.CoordsDto;

public class Util {
    private Util() {
    }

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
        final double normalized = clamp(0.0, value / max, 1.0);
        return scale(from, normalized, to);
    }

    public static int manhattanDistance(CoordsDto lhs, int x, int y) {
        return Math.abs(lhs.getX() - x) + Math.abs(lhs.getY() - y);
    }

    public static int manhattanDistance(CoordsDto lhs, CoordsDto rhs) {
        return manhattanDistance(lhs, rhs.getX(), rhs.getY());
    }
}
