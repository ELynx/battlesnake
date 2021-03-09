package ru.elynx.battlesnake.engine.math;

public class DoubleMatrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final int width;
    private final int height;
    private final int length;

    private final int[] offset1st;
    private final int[] offset2nd;

    private final double[] directValues;
    private final double[] splashValues;
    private final double outsideValue;

    protected DoubleMatrix(int width, int height, double outsideValue) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;

        this.offset1st = new int[4];
        this.offset1st[0] = -this.width;
        this.offset1st[1] = -1;
        this.offset1st[2] = 1;
        this.offset1st[3] = this.width;

        this.offset2nd = new int[8];
        this.offset2nd[0] = this.offset1st[0] - 1;
        this.offset2nd[1] = this.offset1st[0];
        this.offset2nd[2] = this.offset1st[0] + 1;
        this.offset2nd[3] = this.offset1st[1];
        this.offset2nd[4] = this.offset1st[2];
        this.offset2nd[5] = this.offset1st[3] - 1;
        this.offset2nd[6] = this.offset1st[3];
        this.offset2nd[7] = this.offset1st[3] + 1;

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

    public boolean splash1stOrder(int x, int y, double valueAtImpact) {
        return splash1stOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        final int impactIndex = safeIndex(x, y);

        // if impact is out of matrix ignore the setter
        if (impactIndex >= 0) {
            unsafeSetDirectValue(impactIndex, valueAtImpact);

            valueAtImpact = valueAtImpact / denominator;

            for (int offset : offset1st) {
                final int splashIndex = impactIndex + offset;
                if (splashIndex >= 0 && splashIndex < length) {
                    unsafeAddSplashValue(splashIndex, valueAtImpact);
                }
            }

            return true;
        }

        return false;
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact) {
        return splash2ndOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        final int impactIndex = safeIndex(x, y);

        // if impact is out of matrix ignore the setter
        if (impactIndex >= 0) {
            unsafeSetDirectValue(impactIndex, valueAtImpact);

            final double splash1st = valueAtImpact / denominator;
            final double splash2nd = splash1st / denominator;

            boolean flipFlop = false;
            final int skipFlipFlop = offset2nd.length / 2 - 1;
            for (int i = 0; i < offset2nd.length; ++i) {
                final int splashIndex = impactIndex + offset2nd[i];
                if (splashIndex >= 0 && splashIndex < length) {
                    if (flipFlop) {
                        unsafeAddSplashValue(splashIndex, splash1st);
                    } else {
                        unsafeAddSplashValue(splashIndex, splash2nd);
                    }
                }

                if (i != skipFlipFlop) {
                    flipFlop = !flipFlop;
                }
            }

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
