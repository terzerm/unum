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
package org.decimal4j.dfloat.ops;

public enum Remainder {
    ZERO,
    GREATER_THAN_ZERO_BUT_LESS_THAN_HALF,
    EQUAL_TO_HALF,
    GREATER_THAN_HALF;

    public final boolean isZero() {
        return this == ZERO;
    }
    public final boolean isGreaterThanZero() {
        return this != ZERO;
    }
    public final boolean isLessThanHalf() {
        return this == ZERO | this == GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
    }
    public final boolean isEqualToHalf() {
        return this == EQUAL_TO_HALF;
    }
    public final boolean isGreaterThanHalf() {
        return this == GREATER_THAN_HALF;
    }
    public static final Remainder ofDigit(final int digit) {
        if (digit == 0) return ZERO;
        if (digit > 5) return GREATER_THAN_HALF;
        if (digit < 5) return GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
        return EQUAL_TO_HALF;
    }
    public static final Remainder ofPow10(final long remainder, final int n) {
        if (remainder == 0) return ZERO;
        if (n < POW10_HALF.length) {
            final long pow10half = POW10_HALF[n];
            if (remainder > pow10half) return GREATER_THAN_HALF;
            if (remainder < pow10half) return GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
            return EQUAL_TO_HALF;
        }
        return GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
    }

    private static final long[] POW10_HALF = {
            0,
            5,
            50,
            500,
            5000,
            50000,
            500000,
            5000000,
            50000000,
            500000000,
            5000000000L,
            50000000000L,
            500000000000L,
            5000000000000L,
            50000000000000L,
            500000000000000L,
            5000000000000000L,
            50000000000000000L,
            500000000000000000L,
            5000000000000000000L
    };
}
