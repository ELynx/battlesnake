package ru.elynx.battlesnake.engine.math;

public class DoubleMatrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final int width;
    private final int height;
    private final int length;
    private final double[] directValues;
    private final double[] splashValues;
    private final double outsideValue;

    protected DoubleMatrix(int width, int height, double outsideValue) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;
        this.directValues = new double[this.length];
        this.splashValues = new double[this.length];
        this.outsideValue = outsideValue;
    }

    public static DoubleMatrix uninitializedMatrix(int width, int height, double outsideValue) {
        return new DoubleMatrix(width, height, outsideValue);
    }

    public static DoubleMatrix zeroMatrix(int width, int height, double outsideValue) {
        DoubleMatrix result = uninitializedMatrix(width, height, outsideValue);
        result.zero();
        return result;
    }

    public void zero() {
        for (int i = 0; i < length; ++i) {
            directValues[i] = Double.NaN;
            splashValues[i] = Double.NaN;
        }
    }

    public double getValue(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return outsideValue;

        return unsafeGetValue(index);
    }

    public boolean setValue(int x, int y, double value) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return false;

        unsafeSetDirectValue(index, value);
        return true;
    }

    protected void addSplashValue(int x, int y, double value) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return;

        unsafeAddSplashValue(index, value);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact) {
        return splash1stOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        // if impact is out of matrix ignore the setter
        if (setValue(x, y, valueAtImpact)) {
            valueAtImpact = valueAtImpact / denominator;

            addSplashValue(x, y - 1, valueAtImpact);
            addSplashValue(x - 1, y, valueAtImpact);
            addSplashValue(x + 1, y, valueAtImpact);
            addSplashValue(x, y + 1, valueAtImpact);

            return true;
        }

        return false;
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact) {
        return splash2ndOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact, double denominator) {
        if (splash1stOrder(x, y, valueAtImpact, denominator)) {
            valueAtImpact = valueAtImpact / denominator / denominator;

            addSplashValue(x - 1, y - 1, valueAtImpact);
            addSplashValue(x + 1, y - 1, valueAtImpact);
            addSplashValue(x - 1, y + 1, valueAtImpact);
            addSplashValue(x + 1, y + 1, valueAtImpact);

            return true;
        }

        return false;
    }

    protected int unsafeIndex(int x, int y) {
        return x + width * y;
    }

    protected int safeIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return unsafeIndex(x, y);
    }

    protected double unsafeGetValue(int index) {
        if (Double.isNaN(directValues[index])) {
            if (Double.isNaN(splashValues[index])) {
                return 0.0d;
            } else {
                return splashValues[index];
            }
        } else {
            return directValues[index];
        }
    }

    protected void unsafeSetDirectValue(int index, double value) {
        directValues[index] = value;
    }

    protected void unsafeAddSplashValue(int index, double value) {
        if (Double.isNaN(splashValues[index])) {
            splashValues[index] = value;
        } else {
            splashValues[index] += value;
        }
    }
}
