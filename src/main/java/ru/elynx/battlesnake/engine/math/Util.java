package ru.elynx.battlesnake.engine.math;

import ru.elynx.battlesnake.protocol.CoordsDto;

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

    public static int manhattanDistance(CoordsDto lhs, CoordsDto rhs) {
        return manhattanDistance(lhs, rhs.getX(), rhs.getY());
    }

    public static int manhattanDistance(CoordsDto lhs, int x, int y) {
        return manhattanDistance(lhs.getX(), lhs.getY(), x, y);
    }

    private static int manhattanDistance(int lhsX, int lhsY, int rhsX, int rhsY) {
        return Math.abs(lhsX - rhsX) + Math.abs(lhsY - rhsY);
    }
}
