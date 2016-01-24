/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.dfloat.api;

import org.decimal4j.dfloat.ops.Remainder;

import java.math.RoundingMode;

/**
 * IEEE 754-2008 rounding directions.
 * <p>
 * Except where stated otherwise, every operation is performed as if it first produced an intermediate
 * result correct to infinite precision and with unbounded range, and then rounded that result according to
 * one of the rounding direction constants.
 * <p>
 * The rounding-direction affects all computational operations that might be inexact. Inexact numeric
 * floating-point results always have the same sign as the unrounded result. The rounding-direction affects
 * the signs of exact zero sums, and also affects the thresholds beyond which overflow and underflow are
 * signaled.
 */
public enum RoundingDirection {
    /**
     * The floating-point number nearest to the infinitely precise result is returned; if the two nearest
     * floating-point numbers bracketing an unrepresentable infinitely precise result are equally near, the
     * one with an even least significant digit is returned.
     */
    NearestTiesToEven {
        @Override
        public final int getRoundingIncrement(final int signum, final int leastSignificantDigit, final Remainder remainder) {
            return remainder.isLessThanHalf() ? 0 : remainder.isGreaterThanHalf() ? 1 : leastSignificantDigit & 0x1;
        }
    },
    /**
     * The floating-point number nearest to the infinitely precise result is returned; if the two nearest
     * floating-point numbers bracketing an unrepresentable infinitely precise result are equally near, the
     * one with larger magnitude is returned.
     */
    NearestTiesToAway {
        @Override
        public final int getRoundingIncrement(final int signum, final int leastSignificantDigit, final Remainder remainder) {
            return remainder.isLessThanHalf() ? 0 : 1;
        }
    },
    /**
     * The result is the floating-point number closest to and no less than the infinitely precise result
     * (possibly positive infinity).
     */
    TowardPositive {
        @Override
        public final int getRoundingIncrement(final int signum, final int leastSignificantDigit, final Remainder remainder) {
            return signum >= 0 & remainder.isGreaterThanZero() ? 1 : 0;
        }
    },
    /**
     * The result is the floating-point number closest to and no greater than the infinitely precise result
     * (possibly negative infinity).
     */
    TowardNegative {
        @Override
        public final int getRoundingIncrement(final int signum, final int leastSignificantDigit, final Remainder remainder) {
            return signum <= 0 & remainder.isGreaterThanZero() ? 1 : 0;
        }
    },
    /**
     * The result is the floating-point number closest to and no greater in magnitude than the infinitely
     * precise result.
     */
    TowardZero {
        @Override
        public final int getRoundingIncrement(final int signum, final int leastSignificantDigit, final Remainder remainder) {
            return 0;
        }
    };

    /**
     * Default rounding direction is nearest with ties to even.
     */
    public static final RoundingDirection DEFAULT = NearestTiesToEven;

    /**
     * Returns a rounding direction given a rounding mode, or throws an exception if no rounding direction
     * is defined for the specified rounding mode.
     *
     * @param roundingMode the rounding mode to transform into a rounding direction
     * @return the rounding direction equivalent to the given rounding mode
     * @throws IllegalArgumentException if no rounding direction is defined for the given rounding mode
     */
    public static RoundingDirection forRoundingMode(final RoundingMode roundingMode) {
        switch (roundingMode) {
            case HALF_EVEN:
                return NearestTiesToEven;
            case HALF_UP:
                return NearestTiesToAway;
            case CEILING:
                return TowardPositive;
            case FLOOR:
                return TowardNegative;
            case DOWN:
                return TowardZero;
            default:
                throw new IllegalArgumentException("Unsupported rounding mode: " + roundingMode);
        }
    }

    /**
     * Returns the rounding increment zero or one given the signum, least significant digit and truncated
     * remainder of the result.
     *
     * @param signum                the signum of the result, -1, 0 or 1
     * @param leastSignificantDigit the least significant digit of the truncated result, 0-9
     * @param remainder             the truncated part
     * @return the rounding increment, 0 or 1, to add to the unsigned result
     */
    abstract public int getRoundingIncrement(int signum, final int leastSignificantDigit, final Remainder remainder);
}
