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

import java.io.Serializable;

/**
 * A Universal number backed by a double.
 */
public class DUnum extends AbstractUnum implements Serializable, Comparable<DUnum> {

    private static final long UBIT_MASK = 0x0000000000000001L;

    public static final DUnum ZERO  = new DUnum(0.0);
    public static final DUnum ONE   = new DUnum(1.0);
    public static final DUnum TWO   = new DUnum(2.0);
    public static final DUnum TEN   = new DUnum(10.0);
    public static final DUnum QNAN  = new DUnum(DoubleConsts.QNAN);
    public static final DUnum SNAN  = new DUnum(DoubleConsts.SNAN);

    private final double value;

    private DUnum(final double value) {
        this.value = value;
    }

    public static final DUnum valueOf(final double value) {
        return new DUnum(value);
    }

    public static final DUnum exactValueOf(final double value) {
        return new DUnum(exact(value));
    }

    public static final double exact(final double value) {
        final long raw = Double.doubleToRawLongBits(value);
        if (Double.isFinite(value)) {
            if (0 == (raw & UBIT_MASK)) {
                return value;
            }
            return Double.longBitsToDouble(raw & ~UBIT_MASK);
        }
        //NaN or Infinite
        return raw >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

    public static final DUnum inexactValueOf(final double value) {
        return new DUnum(inexact(value));
    }
    public static final double inexact(final double value) {
        final long raw = Double.doubleToRawLongBits(value);
        if (Double.isFinite(value)) {
            if (0 != (raw & UBIT_MASK)) {
                return value;
            }
            return Double.longBitsToDouble(raw | UBIT_MASK);
        }
        //NaN or Infinite
        return raw >= 0 ? DoubleConsts.QNAN : DoubleConsts.SNAN;
    }

    @Override
    public long longValue() {
        return (long)value;
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
        return value < 0;
    }

    @Override
    public boolean isPositive() {
        return value > 0;
    }

    @Override
    public boolean isSignNegative() {
        return isSignNegative(value);
    }

    public static boolean isSignNegative(final double value) {
        return value < 0 || Double.doubleToRawLongBits(value) < 0;
    }

    @Override
    public boolean isZero() {
        return value == 0;
    }

    public DUnum nextUp() {
        return new DUnum(nextUp(value));
    }

    public static double nextUp(final double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return DoubleConsts.QNAN;
        }
        if (Double.isNaN(value)) {
            return isSignNegative(value) ? Double.NEGATIVE_INFINITY : DoubleConsts.QNAN;
        }
        return Math.nextUp(value);
    }

    public DUnum nextDown() {
        return new DUnum(nextDown(value));
    }

    public static double nextDown(final double value) {
        if (value == Double.NEGATIVE_INFINITY) {
            return DoubleConsts.SNAN;
        }
        if (Double.isNaN(value)) {
            return isSignNegative(value) ? DoubleConsts.SNAN: Double.POSITIVE_INFINITY;
        }
        return Math.nextDown(value);
    }
    @Override
    public DUnum getLowerBound() {
        if (isPositive() | isExact() | isNaN()) {
            return this;
        }
        return new DUnum(Math.nextDown(value));
    }

    public static double getLowerBound(final double value) {
        if (value > 0 | isExact(value) | Double.isNaN(value)) {
            return value;
        }
        return Math.nextDown(value);
    }

    @Override
    public DUnum getUpperBound() {
        if (isNegative() | isExact() | isNaN()) {
            return this;
        }
        return new DUnum(Math.nextUp(value));
    }

    public static double getUpperBound(final double value) {
        if (value < 0 | isExact(value) | Double.isNaN(value)) {
            return value;
        }
        return Math.nextUp(value);
    }

    @Override
    public int compareTo(DUnum o) {
        return compare(value, o.value);
    }

    public int compare(final double a, final double b) {
        if (a < b) return -1;
        if (a > b) return 1;

        final boolean aNotNaN = a == a;
        final boolean bNotNaN = b == b;

        //equal, but exclude NaN's
        if (a == b & aNotNaN & bNotNaN) {
            return 0;
        }

        //at least one is NaN
        final long araw = Double.doubleToRawLongBits(a);
        final long braw = Double.doubleToRawLongBits(b);
        if (araw == braw) {
            return 0;
        }
        if (araw < 0 & braw >= 0) return -1;
        if (braw < 0 & araw >= 0) return 1;

        //same sign
        if (aNotNaN) {
            return araw >= 0 ? -1 : 1;
        }
        if (bNotNaN) {
            return braw >= 0 ? 1 : -1;
        }

        //both NaN
        return 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (getClass() == obj.getClass()) {
            return 0 == compareTo((DUnum)obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(value);
    }

    public static String toString(double value) {
        if (isExact(value)) {
            return String.valueOf(value);
        }
        if (Double.isNaN(value)) {
            return Double.doubleToRawLongBits(value) >= 0 ? "qNaN" : "sNaN";
        }
        if (value >= 0) {
            return "(" + exact(value) + ", " + nextUp(value) + ")";
        } else {
            return "(" + nextDown(value) + ", " + exact(value) + ")";
        }
    }

    public static void main(String... args) {
        System.out.println("*** valueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DUnum.valueOf(i));
        }
        System.out.println("*** exactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DUnum.exactValueOf(i));
        }
        System.out.println("*** inexactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(DUnum.inexactValueOf(i));
        }
        System.out.println("*** valueOf/nextDown");
        for (int i = -10; i < 10; i++) {
            System.out.println(DUnum.valueOf(Math.nextDown((double)i)));
        }
        System.out.println("*** specials");
        System.out.println(+0.0);
        System.out.println(-0.0);
        System.out.println(DUnum.valueOf(Double.POSITIVE_INFINITY));
        System.out.println(DUnum.valueOf(Double.NEGATIVE_INFINITY));
        System.out.println(DUnum.QNAN);
        System.out.println(DUnum.SNAN);
    }
}
