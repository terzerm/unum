/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 tools4j-unum, Marco Terzer
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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * An universial number where the fraction fits in a 64bit long value. The fraction size is therefore at most 64 bits
 * and the fraction size contains 6 bits. The exponent fits in 16 bits and the exponent size contains 4 bits.
 */
public class LongUnum extends AbstractUnum<LongUnum> implements Serializable {

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
    public static final LongUnum INF = new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION, UBIT_EXACT, (byte)16, (byte)64);
    public static final LongUnum NAN = new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION, UBIT_INEXACT, (byte)16, (byte)64);

    private final byte sign;
    private final int exponent;
    private final long fraction;
    private final byte ubit;
    private final byte exponentSize;
    private final byte fractionSize;

    private LongUnum(final byte sign, final int exponent, final long fraction, final byte ubit, final byte exponentSize, final byte fractionSize) {
        validate(sign, exponent, fraction, ubit, exponentSize, fractionSize);
        this.sign = sign;
        this.exponent = exponent;
        this.fraction = fraction;
        this.ubit = ubit;
        this.exponentSize = exponentSize;
        this.fractionSize = fractionSize;
    }

    private static void validate(final byte sign, final int exponent, final long fraction, final byte ubit, final byte exponentSize, final byte fractionSize) {
        if (sign < -1 | sign > 0) {
            throw new IllegalArgumentException("invalid sign: " + sign);
        }
        if (ubit < 0 | ubit > 1) {
            throw new IllegalArgumentException("invalid ubit: " + ubit);
        }
        if (exponentSize < 1 | exponentSize > 16) {
            throw new IllegalArgumentException("invalid exponentSize: " + exponentSize);
        }
        if (fractionSize < 1 | fractionSize > 64) {
            throw new IllegalArgumentException("invalid fractionSize: " + fractionSize);
        }
        if (exponent < 0 || (exponent >= (1 << exponentSize))) {
            throw new IllegalArgumentException("invalid exponent " + exponent + " for exponentSize=" + exponentSize);
        }
        if ((fractionSize == 63 & fraction < 0) || (fractionSize < 63 & fraction >= (1L << fractionSize))) {
            throw new IllegalArgumentException("invalid fraction " + fraction + " for fractionSize=" + fractionSize);
        }
    }

    @Override
    public Factory<LongUnum> getFactory() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Factory<Ubound<LongUnum>> getUboundFactory() {
        return null;
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
        return sign > 0 & !isZero() & !isNaN();
    }

    @Override
    public boolean isNegative() {
        return sign < 0 & !isZero() & !isNaN();
    }

    @Override
    public boolean isSignNegative() {
        return sign < 0;
    }

    @Override
    public boolean isZero() {
        return exponent == 0 & fraction == 0;
    }

    @Override
    public boolean isNonNegative() {
        return (sign > 0 & !isNaN()) | (exponent == 0 & fraction == 0);
    }

    @Override
    public boolean isNonPositive() {
        return (sign < 0 & !isNaN()) | (exponent == 0 & fraction == 0);
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
    public LongUnum getLowerBound() {
        if (isExact() || isPositive()) {
            return this;
        }
        return nextExact();
    }

    @Override
    public LongUnum getUpperBound() {
        if (isExact() || isNegative()) {
            return this;
        }
        return nextExact();
    }

    @Override
    public LongUnum nextDown() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum nextUp() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum intervalWidth() {
        throw new RuntimeException("not implemented");
    }

    private LongUnum nextExact() {
        if (fraction != MAX_FRACTION) {
            return new LongUnum(sign, exponent, fraction + 1, UBIT_EXACT, exponentSize, fractionSize);
        }
        if (exponent != MAX_EXPONENT) {
            return new LongUnum(sign, exponent + 1, 0, UBIT_EXACT, exponentSize, fractionSize);
        }
        //NaN
        return this;
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
                return doubleValueExact(sign, fraction, fractionSize, exponent, exponentSize);
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
        return sign >= 0 ? Doubles.QNAN : Doubles.SNAN;
    }

    public BigDecimal bigDecimalValueExact() {
        if (isExact() && isFinite()) {
            return bigDecimalValueExact(sign, fraction, fractionSize, exponent, exponentValue());
        }
        throw new ArithmeticException("not exact or not finite: " + this);
    }

    private int exponentValue() {
        return exponentValue(exponent, exponentSize);
    }
    private static int exponentValue(final int exponent, final int exponentSize) {
        final int bias = (1 << (exponentSize - 1)) - 1;
        final int hidden = exponent == 0 ? 0 : 1;
        return exponent - bias + 1 - hidden;
    }

    private static double doubleValueExact(final byte sign, final long fraction, final int fractionSize, final int exponent, final int exponentSize) {
        final int hidden = exponent == 0 ? 0 : 1;
        return doubleValueExact(sign, hidden, fraction, fractionSize, exponentValue(exponent, exponentSize));
    }
    private static double doubleValueExact(final byte sign, final int hidden, final long fraction, final int fractionSize, final int exponentvalue) {
        final double abs = Math.scalb(hidden + Math.scalb((double)fraction, -fractionSize), exponentvalue);
        return sign >= 0 ? abs : -abs;
    }

    private static BigDecimal bigDecimalValueExact(final byte sign, final long fraction, final int fractionSize, final int exponent, final int expovalue) {
        final BigDecimal two = BigDecimal.valueOf(2);
        final BigDecimal bigFraction = fraction >= 0 ? BigDecimal.valueOf(fraction) : BigDecimal.valueOf(fraction >>> 1).multiply(two).add(BigDecimal.valueOf(fraction & 0x1));
        final BigDecimal scaledFraction = bigFraction.divide(two.pow(fractionSize));
        final BigDecimal scaledFractionWithHiddenBit = exponent == 0 ? scaledFraction : BigDecimal.ONE.add(scaledFraction);
        final BigDecimal abs = scaledFractionWithHiddenBit.multiply(two.pow(expovalue)).stripTrailingZeros();
        return sign >= 0 ? abs : abs.negate();
    }

    @Override
    public int compareTo(LongUnum o) {
        return 0;
    }

    @Override
    public LongUnum min(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum max(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum add(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum subtract(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum multiply(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum divide(LongUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public LongUnum negate() {
        return isNaN() ? this : new LongUnum((byte)-sign, exponent, fraction, ubit, exponentSize, fractionSize);
    }

    @Override
    public LongUnum abs() {
        return isNaN() | isPositive() ? this : negate();
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
                final int bias = (1 << (exponentSize - 1)) - 1;
                final int hidden = exponent == 0 ? 0 : 1;
                final int expovalue = exponent - bias + 1 - hidden;
                if (0 == (fraction & EXACT_DOUBLE_FRACTION_MASK)) {
                    if (expovalue >= Double.MIN_EXPONENT & expovalue <= Double.MAX_EXPONENT) {
                        return Double.toString(doubleValueExact(sign, hidden, fraction, fractionSize, expovalue));
                    }
                }
                return bigDecimalValueExact(sign, fraction, fractionSize, exponent, expovalue).toPlainString();

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
        System.out.println("(1*)=\t" + new LongUnum(SIGN_POSITIVE, 0, 1, UBIT_INEXACT, (byte)1, (byte)1));
        System.out.println("(2*)=\t" + new LongUnum(SIGN_POSITIVE, 1, 0, UBIT_INEXACT, (byte)1, (byte)1));
        System.out.println("(3*)=\t" + new LongUnum(SIGN_POSITIVE, 1, 1, UBIT_INEXACT, (byte)1, (byte)1));
        System.out.println("(e=2^65535, f= 1.0)=\t" + new LongUnum(SIGN_POSITIVE, MAX_EXPONENT-1, 0, UBIT_EXACT, (byte)16, (byte)63));
        System.out.println("(e=2^65536, f= 1.0)=\t" + new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, 0, UBIT_EXACT, (byte)16, (byte)63));
        System.out.println("(e=2^65535, f=2^64)=\t" + new LongUnum(SIGN_POSITIVE, MAX_EXPONENT-1, MAX_FRACTION, UBIT_EXACT, (byte)16, (byte)64));
        System.out.println("(e=2^65536, f=2^63)=\t" + new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION>>>1, UBIT_EXACT, (byte)16, (byte)63));
        System.out.println("(e=2^65536, f~2^64)=\t" + new LongUnum(SIGN_POSITIVE, MAX_EXPONENT, MAX_FRACTION^2, UBIT_EXACT, (byte)16, (byte)64));
    }
}
