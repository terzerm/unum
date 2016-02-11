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
package org.decimal4j.dfloat.attribute;

import org.decimal4j.dfloat.api.FloatMath;
import org.decimal4j.dfloat.encode.Decimal64;
import org.decimal4j.dfloat.ops.Remainder;
import org.decimal4j.dfloat.ops.Sign;

import java.math.RoundingMode;

/**
 * IEEE 754-2008 rounding directions.
 *
 * Except where stated otherwise, every operation is performed as if it first produced an intermediate
 * result correct to infinite precision and with unbounded range, and then rounded that result according to
 * one of the rounding direction constants.
 *
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
        public final int getRoundingIncrement(final long signumValue, final int leastSignificantDigit, final Remainder remainder) {
            return remainder.isLessThanHalf() ? 0 : remainder.isGreaterThanHalf() ? 1 : leastSignificantDigit & 0x1;
        }
        @Override
        public final boolean isRoundingIncrementPossible(long signumValue) {
            return true;
        }
        @Override
        public final long roundOverflow(long signumValue) {
            return Sign.copySign(Decimal64.INF, signumValue);
        }
    },
    /**
     * The floating-point number nearest to the infinitely precise result is returned; if the two nearest
     * floating-point numbers bracketing an unrepresentable infinitely precise result are equally near, the
     * one with larger magnitude is returned.
     */
    NearestTiesToAway {
        @Override
        public final int getRoundingIncrement(final long signumValue, final int leastSignificantDigit, final Remainder remainder) {
            return remainder.isLessThanHalf() ? 0 : 1;
        }
        @Override
        public final boolean isRoundingIncrementPossible(long signumValue) {
            return true;
        }
        @Override
        public final long roundOverflow(long signumValue) {
            return Sign.copySign(Decimal64.INF, signumValue);
        }
    },
    /**
     * The result is the floating-point number closest to and no less than the infinitely precise result
     * (possibly positive infinity).
     */
    TowardPositive {
        @Override
        public final int getRoundingIncrement(final long signumValue, final int leastSignificantDigit, final Remainder remainder) {
            return signumValue >= 0 & remainder.isGreaterThanZero() ? 1 : 0;
        }
        @Override
        public final boolean isRoundingIncrementPossible(long signumValue) {
            return signumValue >= 0;
        }
        @Override
        public final long roundOverflow(long signumValue) {
            return signumValue >= 0 ? Decimal64.INF : Sign.copySign(FloatMath.MAX_NORMAL, -1);
        }
    },
    /**
     * The result is the floating-point number closest to and no greater than the infinitely precise result
     * (possibly negative infinity).
     */
    TowardNegative {
        @Override
        public final int getRoundingIncrement(final long signumValue, final int leastSignificantDigit, final Remainder remainder) {
            return signumValue <= 0 & remainder.isGreaterThanZero() ? 1 : 0;
        }
        @Override
        public final boolean isRoundingIncrementPossible(long signumValue) {
            return signumValue <= 0;
        }
        @Override
        public final long roundOverflow(long signumValue) {
            return signumValue < 0 ? Sign.copySign(Decimal64.INF, -1) : FloatMath.MAX_NORMAL;
        }
    },
    /**
     * The result is the floating-point number closest to and no greater in magnitude than the infinitely
     * precise result.
     */
    TowardZero {
        @Override
        public final int getRoundingIncrement(final long signumValue, final int leastSignificantDigit, final Remainder remainder) {
            return 0;
        }
        @Override
        public final boolean isRoundingIncrementPossible(long signumValue) {
            return false;
        }
        @Override
        public final long roundOverflow(long signumValue) {
            return Sign.copySign(FloatMath.MAX_NORMAL, signumValue);
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

    private final Attributes attributes = new RoundingAttributes(this);

    /**
     * Returns an attributes object with {@link Attributes#getBinaryRoundingDirection() binary}
     * and {@link Attributes#getDecimalRoundingDirection() decimal} rounding mode both set to this
     * rounding direction. All other attributes are set to {@link Attributes#DEFAULT default} values.
     *
     * @return Default attributes but with this rounding direction for both binary and decimal values
     * @see Attributes#DEFAULT
     */
    public final Attributes asAttributes() {
        return attributes;
    }

    /**
     * Returns the rounding increment zero or one given the signum value, least significant digit and truncated
     * remainder of the result.
     *
     * @param signumValue           the signum value of the result, negative, zero or positive
     * @param leastSignificantDigit the least significant digit of the truncated result, 0-9
     * @param remainder             the truncated part
     * @return the rounding increment, 0 or 1, to add to the unsigned result
     */
    abstract public int getRoundingIncrement(long signumValue, final int leastSignificantDigit, final Remainder remainder);

    /**
     * Returns true if a rounding increment is possible given only the signum value of the result.
     *
     * @param signumValue           the signum value of the result, negative, zero or positive
     * @return true if rounding increment 1 is possible with the given signumValue, and false if it is always 0
     */
    abstract public boolean isRoundingIncrementPossible(long signumValue);

    /**
     * Returns the rounded overflow value.
     *
     * @param signumValue           the signum value of the result, negative or positive
     * @return overflow value rounded according to IEEE 754-2008 section 7.4.
     */
    abstract public long roundOverflow(long signumValue);
}