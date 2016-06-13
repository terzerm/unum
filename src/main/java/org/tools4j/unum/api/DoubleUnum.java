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

/**
 * A Universal number backed by a double.
 */
public class DoubleUnum extends AbstractUnum<DoubleUnum> implements Serializable {

    private static final long UBIT_MASK = 0x0000000000000001L;

    public static final DoubleUnum ZERO  = new DoubleUnum(0.0);
    public static final DoubleUnum ONE   = new DoubleUnum(1.0);
    public static final DoubleUnum TWO   = new DoubleUnum(2.0);
    public static final DoubleUnum TEN   = new DoubleUnum(10.0);
    public static final DoubleUnum QNAN  = new DoubleUnum(Doubles.QNAN);
    public static final DoubleUnum SNAN  = new DoubleUnum(Doubles.SNAN);

    public static final Ubound<DoubleUnum> UBOUND_ZERO = Ubound.create(ZERO);
    public static final Ubound<DoubleUnum> UBOUND_ONE = Ubound.create(ONE);
    public static final Ubound<DoubleUnum> UBOUND_QNAN = Ubound.create(QNAN);
    public static final Ubound<DoubleUnum> UBOUND_SNAN = Ubound.create(SNAN);

    public static final Factory<DoubleUnum> FACTORY = new Factory<DoubleUnum>() {
        @Override
        public DoubleUnum qNaN() {
            return QNAN;
        }

        @Override
        public DoubleUnum sNaN() {
            return SNAN;
        }

        @Override
        public DoubleUnum zero() {
            return ZERO;
        }

        @Override
        public DoubleUnum one() {
            return ONE;
        }
    };

    public static final Factory<Ubound<DoubleUnum>> UBOUND_FACTORY = new Factory<Ubound<DoubleUnum>>() {
        @Override
        public Ubound<DoubleUnum> qNaN() {
            return UBOUND_QNAN;
        }
        @Override
        public Ubound<DoubleUnum> sNaN() {
            return UBOUND_SNAN;
        }
        @Override
        public Ubound<DoubleUnum> zero() {
            return UBOUND_ZERO;
        }
        @Override
        public Ubound<DoubleUnum> one() {
            return UBOUND_ONE;
        }
    };
    public static final DoubleUnum signedNaN(final double sign) {
        return isSignNegative(sign) ? SNAN : QNAN;
    }

    private final double value;

    private DoubleUnum(final double value) {
        this.value = value;
    }

    public static final DoubleUnum valueOf(final double value) {
        return new DoubleUnum(value);
    }

    public static final DoubleUnum exactValueOf(final double value) {
        return new DoubleUnum(exact(value));
    }

    public static final double exact(final double value) {
        final long raw = Double.doubleToRawLongBits(value);
        if (Double.isFinite(value)) {
            if (0 == (raw & UBIT_MASK)) {
                return value;
            }
            return Double.longBitsToDouble(raw >= 0 ? raw+1 : raw-1);
        }
        //NaN or Infinite
        return raw >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

    public static final DoubleUnum inexactValueOf(final double value) {
        return new DoubleUnum(inexact(value));
    }
    public static final double inexact(final double value) {
        final long raw = Double.doubleToRawLongBits(value);
        if (Double.isFinite(value)) {
            if (0 != (raw & UBIT_MASK)) {
                return value;
            }
            return Double.longBitsToDouble(raw >= 0 ? raw+1 : raw-1);
        }
        //NaN or Infinite
        return raw >= 0 ? Doubles.QNAN : Doubles.SNAN;
    }

    @Override
    public Factory<DoubleUnum> getFactory() {
        return FACTORY;
    }

    @Override
    public Factory<Ubound<DoubleUnum>> getUboundFactory() {
        return UBOUND_FACTORY;
    }

    @Override
    public int intValue() {
        return (int)value;
    }

    @Override
    public long longValue() {
        return (long)value;
    }

    @Override
    public float floatValue() {
        return (float)value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean isNaN() {
        return Double.isNaN(value);
    }

    @Override
    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    @Override
    public boolean isFinite() {
        return Double.isFinite(value);
    }

    @Override
    public boolean isExact() {
        return isExact(value);
    }

    public static boolean isExact(final double value) {
        return !Double.isNaN(value) & 0 == (Double.doubleToRawLongBits(value) & UBIT_MASK);
    }

    @Override
    public boolean isInexact() {
        return isInexact(value);
    }

    public static boolean isInexact(final double value) {
        return Double.isNaN(value) | 0 != (Double.doubleToRawLongBits(value) & UBIT_MASK);
    }

    @Override
    public boolean isNegative() {
        return value < 0.0;
    }

    @Override
    public boolean isPositive() {
        return value > 0.0;
    }

    @Override
    public boolean isSignNegative() {
        return isSignNegative(value);
    }

    public static boolean isSignNegative(final double value) {
        return value < 0.0 || Double.doubleToRawLongBits(value) < 0;
    }

    @Override
    public boolean isZero() {
        return value == 0.0;
    }

    @Override
    public boolean isNonNegative() {
        return value >= 0.0;
    }

    @Override
    public boolean isNonPositive() {
        return value <= 0.0;
    }

    public DoubleUnum nextUp() {
        return new DoubleUnum(nextUp(value));
    }

    public static double nextUp(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return Doubles.QNAN;
        }
        if (Double.isNaN(value)) {
            return isSignNegative(value) ? Double.NEGATIVE_INFINITY : Doubles.QNAN;
        }
        return Math.nextUp(value);
    }

    public DoubleUnum nextDown() {
        return new DoubleUnum(nextDown(value));
    }

    public static double nextDown(final double value) {
        if (value == Double.NEGATIVE_INFINITY) {
            return Doubles.SNAN;
        }
        if (Double.isNaN(value)) {
            return isSignNegative(value) ? Doubles.SNAN: Double.POSITIVE_INFINITY;
        }
        return Math.nextDown(value);
    }
    @Override
    public DoubleUnum getLowerBound() {
        if (isExact() | isNaN()) {
            return this;
        }
        return new DoubleUnum(value > 0.0 ? Math.nextDown(value) : Math.nextUp(value));
    }

    public static double getLowerBound(final double value) {
        if (isExact(value) | Double.isNaN(value)) {
            return value;
        }
        return value > 0.0 ? Math.nextDown(value) : Math.nextUp(value);
    }

    @Override
    public DoubleUnum getUpperBound() {
        if (isExact() | isNaN()) {
            return this;
        }
        return new DoubleUnum(value > 0.0 ? Math.nextUp(value) : Math.nextDown(value));
    }

    public static double getUpperBound(final double value) {
        if (value < 0.0 | isExact(value) | Double.isNaN(value)) {
            return value;
        }
        return value > 0.0 ? Math.nextUp(value) : Math.nextDown(value);
    }

    @Override
    public DoubleUnum intervalWidth() {
        final double size = intervalWidth(value);
        return size == 0 ? ZERO : DoubleUnum.valueOf(size);
    }

    public static double intervalWidth(final double value) {
        if (isExact(value)) {
            return Double.isFinite(value) ? 0.0 : Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(value)) {
            return Doubles.signedNaN(value);
        }
        return nextUp(value) - nextDown(value);
    }

    @Override
    public DoubleUnum add(final DoubleUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public DoubleUnum subtract(final DoubleUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public DoubleUnum multiply(final DoubleUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public DoubleUnum divide(final DoubleUnum other) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int compareTo(final DoubleUnum other) {
        return compare(value, other.value);
    }

    public static int compare(final double a, final double b) {
        if (a < b) return -1;
        if (a > b) return 1;
        if (a == b) return 0;

        //at least one is NaN
        final long araw = Double.doubleToRawLongBits(a);
        final long braw = Double.doubleToRawLongBits(b);
        if (araw == braw) {
            return 0;
        }
        if (araw < 0 & braw >= 0) return -1;
        if (braw < 0 & araw >= 0) return 1;

        //same sign
        if (a == a) {
            //a is not NaN
            return araw >= 0 ? -1 : 1;
        }
        if (b == b) {
            //b is not NaN
            return braw >= 0 ? 1 : -1;
        }

        //both NaN, same sign
        return 0;
    }

    @Override
    public DoubleUnum min(final DoubleUnum other) {
        return compareTo(other) <= 0 ? this : other;
    }

    public static double min(final double a, final double b) {
        return compare(a, b) <= 0 ? a : b;
    }

    @Override
    public DoubleUnum max(final DoubleUnum other) {
        return compareTo(other) >= 0 ? this : other;
    }

    public static double max(final double a, final double b) {
        return compare(a, b) >= 0 ? a : b;
    }

    @Override
    public DoubleUnum negate() {
        return new DoubleUnum(-value);
    }

    @Override
    public DoubleUnum abs() {
        return value >= 0.0 ? this : value < 0.0 ? negate() : this /*NaN*/;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (getClass() == obj.getClass()) {
            return 0 == compareTo((DoubleUnum)obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(value);
    }

    public static String toString(final double value) {
        if (isExact(value)) {
            return String.valueOf(value);
        }
        if (Double.isNaN(value)) {
            return Double.doubleToRawLongBits(value) >= 0 ? "qNaN" : "sNaN";
        }
        if (value >= 0.0) {
            return "(" + exact(value) + ", " + nextUp(value) + ")";
        } else {
            return "(" + nextDown(value) + ", " + exact(value) + ")";
        }
    }

    public static void main(String... args) {
        System.out.println("*** valueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DoubleUnum.valueOf(i));
        }
        System.out.println("*** exactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DoubleUnum.exactValueOf(i));
        }
        System.out.println("*** inexactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DoubleUnum.inexactValueOf(i));
        }
        System.out.println("*** valueOf/nextDown");
        for (int i = -10; i < 10; i++) {
            System.out.println(DoubleUnum.valueOf(Math.nextDown((double)i)));
        }
        System.out.println("*** specials");
        System.out.println(+0.0);
        System.out.println(-0.0);
        System.out.println(DoubleUnum.valueOf(Double.POSITIVE_INFINITY));
        System.out.println(DoubleUnum.valueOf(Double.NEGATIVE_INFINITY));
        System.out.println(DoubleUnum.QNAN);
        System.out.println(DoubleUnum.SNAN);
    }
}
