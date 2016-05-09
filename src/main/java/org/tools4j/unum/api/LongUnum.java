/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 unum4j (tools4j), Marco Terzer
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
package org.tools4j.unum.api;

import java.util.Objects;

/**
 * An universial number where the fraction fits in a 64bit long value. The fraction size is therefore at most 64 bits
 * and the fraction size contains 6 bits. The exponent fits in 16 bits and the exponent size contains 4 bits.
 */
public class LongUnum extends AbstractUnum<Long> {

    private static final byte SIGN_POSITIVE = 0;
    private static final byte SIGN_NEGATIVE = -1;
    private static final byte UBIT_EXACT = 0;
    private static final byte UBIT_INEXACT = 1;
    private static final int MAX_EXPONENT = 0xffff;
    private static final long MAX_FRACTION = 0xffffffffffffffffL;
    private static final long EXACT_DOUBLE_FRACTION_MASK = 0xfff0000000000000L;

    public static final LongUnum ZERO = new LongUnum(SIGN_POSITIVE, 0, 0, UBIT_EXACT, (byte)1, (byte)1);
    public static final LongUnum HALF = new LongUnum(SIGN_POSITIVE, 0, 1, UBIT_EXACT, (byte)1, (byte)2);
    public static final LongUnum ONE = new LongUnum(SIGN_POSITIVE, 0, 1, UBIT_EXACT, (byte)1, (byte)1);
    public static final LongUnum TWO = new LongUnum(SIGN_POSITIVE, 1, 0, UBIT_EXACT, (byte)1, (byte)1);
    public static final LongUnum TEN = new LongUnum(SIGN_POSITIVE, 6, 2, UBIT_EXACT, (byte)3, (byte)3);
    public static final LongUnum INF = new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION, UBIT_EXACT, (byte)1, (byte)1);
    public static final LongUnum NAN = new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION, UBIT_INEXACT, (byte)1, (byte)1);

    private final byte sign;
    private final int exponent;
    private final long fraction;
    private final byte ubit;
    private final byte exponentSize;
    private final byte fractionSize;

    private LongUnum(final byte sign, final int exponent, final long fraction, final byte ubit, final byte exponentSize, final byte fractionSize) {
        this.sign = sign;
        this.exponent = exponent;
        this.fraction = fraction;
        this.ubit = ubit;
        this.exponentSize = exponentSize;
        this.fractionSize = fractionSize;
    }

    @Override
    public int getExponent() {
        return exponent;
    }

    @Override
    public int getExponentSize() {
        return exponentSize;
    }

    @Override
    public Long getFraction() {
        return fraction;
    }

    @Override
    public int getFractionSize() {
        return fractionSize;
    }

    @Override
    public boolean isNaN() {
        return ubit == UBIT_INEXACT & exponent == MAX_EXPONENT & fraction == MAX_FRACTION;
    }

    @Override
    public boolean isInfinite() {
        return ubit == UBIT_EXACT & exponent == MAX_EXPONENT & fraction == MAX_FRACTION;
    }

    @Override
    public boolean isFinite() {
        return exponent != MAX_EXPONENT | fraction != MAX_FRACTION;
    }

    @Override
    public boolean isPositive() {
        return sign > 0 & !isZero();
    }

    @Override
    public boolean isNegative() {
        return sign < 0 & !isZero();
    }

    @Override
    public boolean isZero() {
        return exponent == 0 & fraction == 0;
    }

    @Override
    public boolean isExact() {
        return ubit == UBIT_EXACT;
    }

    @Override
    public boolean isInexact() {
        return ubit == UBIT_INEXACT;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return doubleValue(sign, exponent, fraction, ubit, exponentSize, fractionSize);
    }
    private static double doubleValue(final byte sign, final int exponent, final long fraction, final byte ubit, final byte exponentSize, final byte fractionSize) {
        if (ubit == 0) {
            //exact
            if (exponent != MAX_EXPONENT | fraction != MAX_FRACTION) {
                //finite
                final int bias = (1 << (exponentSize-1)) - 1;
                final int hidden = exponent == 0 ? 0 : 1;
                final int expovalue = exponent - bias + 1 - hidden;
                final double abs = Math.scalb(hidden + Math.scalb(fraction, -fractionSize), expovalue);
                return sign >= 0 ? abs : -abs;
            }
            //infinite
            return sign >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        if (fraction != MAX_FRACTION) {
            //inexact finite
            return (doubleValue(sign, exponent, fraction, UBIT_EXACT, exponentSize, fractionSize) +
                    doubleValue(sign, exponent, fraction + 1, UBIT_EXACT, exponentSize, fractionSize))/2;
        }
        if (exponent != MAX_EXPONENT) {
            //inexact finite
            return (doubleValue(sign, exponent, fraction, UBIT_EXACT, exponentSize, fractionSize) +
                    doubleValue(sign, exponent + 1, 0, UBIT_EXACT, exponentSize, fractionSize))/2;
        }
        //NaN
        return sign >= 0 ? DoubleConsts.QNAN : DoubleConsts.SNAN;
    }

    @Override
    public int compareTo(Unum<Long> o) {
        return 0;
    }

    @Override
    public String toString() {
        return toString(sign, exponent, fraction, ubit, exponentSize, fractionSize);
    }
    private static String toString(final byte sign, final int exponent, final long fraction, final byte ubit, final byte exponentSize, final byte fractionSize) {
        if (ubit == 0) {
            //exact
            if (exponent != MAX_EXPONENT | fraction != MAX_FRACTION) {
                //finite
                //FIXME handle double under/overflow
                return Double.toString(doubleValue(sign, exponent, fraction, ubit, exponentSize, fractionSize));
            }
            //infinite
            return sign >= 0 ? "Inf" : "-Inf";
        }
        if (fraction != MAX_FRACTION) {
            //inexact finite
            return "(" + toString(sign, exponent, fraction, UBIT_EXACT, exponentSize, fractionSize) +
                    ", " + toString(sign, exponent, fraction + 1, UBIT_EXACT, exponentSize, fractionSize) + ")";
        }
        if (exponent != MAX_EXPONENT) {
            //inexact finite
            return "(" + toString(sign, exponent, fraction, UBIT_EXACT, exponentSize, fractionSize) +
                    ", " + toString(sign, exponent + 1, 0, UBIT_EXACT, exponentSize, fractionSize) + ")";
        }
        //NaN
        return sign >= 0 ? "qNaN" : "sNaN";
    }

    public static void main(String... args) {
        System.out.println("ZERO=\t" + LongUnum.ZERO);
        System.out.println("HALF=\t" + LongUnum.HALF);
        System.out.println("ONE=\t" + LongUnum.ONE);
        System.out.println("TWO=\t" + LongUnum.TWO);
        System.out.println("TEN=\t" + LongUnum.TEN);
        System.out.println("INF=\t" + LongUnum.INF);
        System.out.println("NAN=\t" + LongUnum.NAN);
    }
}
