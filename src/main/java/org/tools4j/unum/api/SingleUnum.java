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
 * A Universal number backed by a single precision float.
 */
public class SingleUnum extends AbstractUnum<SingleUnum> implements Serializable {

    private static final int UBIT_MASK = 0x00000001;

    public static final SingleUnum ZERO  = new SingleUnum(0.0f);
    public static final SingleUnum ONE   = new SingleUnum(1.0f);
    public static final SingleUnum TWO   = new SingleUnum(2.0f);
    public static final SingleUnum TEN   = new SingleUnum(10.0f);
    public static final SingleUnum QNAN  = new SingleUnum(Singles.QNAN);
    public static final SingleUnum SNAN  = new SingleUnum(Singles.SNAN);

    public static final Factory<SingleUnum> FACTORY = new Factory<SingleUnum>() {
        @Override
        public SingleUnum qNaN() {
            return QNAN;
        }

        @Override
        public SingleUnum sNaN() {
            return SNAN;
        }

        @Override
        public SingleUnum zero() {
            return ZERO;
        }

        @Override
        public SingleUnum one() {
            return ONE;
        }
    };

    public static final SingleUnum signedNaN(final float sign) {
        return isSignNegative(sign) ? SNAN : QNAN;
    }

    private final float value;

    private SingleUnum(final float value) {
        this.value = value;
    }

    public static final SingleUnum valueOf(final float value) {
        return new SingleUnum(value);
    }

    public static final SingleUnum exactValueOf(final float value) {
        return new SingleUnum(exact(value));
    }

    public static final float exact(final float value) {
        final int raw = Float.floatToRawIntBits(value);
        if (Float.isFinite(value)) {
            if (0 == (raw & UBIT_MASK)) {
                return value;
            }
            return Float.intBitsToFloat(raw >= 0 ? raw-1 : raw+1);
        }
        //NaN or Infinite
        return raw >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
    }

    public static final SingleUnum inexactValueOf(final float value) {
        return new SingleUnum(inexact(value));
    }
    public static final float inexact(final float value) {
        final int raw = Float.floatToRawIntBits(value);
        if (Float.isFinite(value)) {
            if (0 != (raw & UBIT_MASK)) {
                return value;
            }
            return Float.intBitsToFloat(raw >= 0 ? raw+1 : raw-1);
        }
        //NaN or Infinite
        return raw >= 0 ? Singles.QNAN : Singles.SNAN;
    }

    @Override
    public Factory<SingleUnum> getFactory() {
        return FACTORY;
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
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean isNaN() {
        return Float.isNaN(value);
    }

    @Override
    public boolean isInfinite() {
        return Float.isInfinite(value);
    }

    @Override
    public boolean isFinite() {
        return Float.isFinite(value);
    }

    @Override
    public boolean isExact() {
        return isExact(value);
    }

    public static boolean isExact(final float value) {
        return !Float.isNaN(value) & 0 == (Float.floatToRawIntBits(value) & UBIT_MASK);
    }

    @Override
    public boolean isInexact() {
        return isInexact(value);
    }

    public static boolean isInexact(final float value) {
        return Float.isNaN(value) | 0 != (Float.floatToRawIntBits(value) & UBIT_MASK);
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

    public static boolean isSignNegative(final float value) {
        return value < 0 || Float.floatToRawIntBits(value) < 0;
    }

    @Override
    public boolean isZero() {
        return value == 0;
    }

    public SingleUnum nextUp() {
        return new SingleUnum(nextUp(value));
    }

    public static float nextUp(final float value) {
        if (value == Float.POSITIVE_INFINITY) {
            return Singles.QNAN;
        }
        if (Float.isNaN(value)) {
            return isSignNegative(value) ? Float.NEGATIVE_INFINITY : Singles.QNAN;
        }
        return Math.nextUp(value);
    }

    public SingleUnum nextDown() {
        return new SingleUnum(nextDown(value));
    }

    public static float nextDown(final float value) {
        if (value == Float.NEGATIVE_INFINITY) {
            return Singles.SNAN;
        }
        if (Float.isNaN(value)) {
            return isSignNegative(value) ? Singles.SNAN: Float.POSITIVE_INFINITY;
        }
        return Math.nextDown(value);
    }

    @Override
    public SingleUnum getLowerBound() {
        if (isExact() | isNaN()) {
            return this;
        }
        return new SingleUnum(value > 0 ? Math.nextDown(value) : Math.nextUp(value));
    }

    public static float getLowerBound(final float value) {
        if (isExact(value) | Float.isNaN(value)) {
            return value;
        }
        return value > 0 ? Math.nextDown(value) : Math.nextUp(value);
    }

    @Override
    public SingleUnum getUpperBound() {
        if (isExact() | isNaN()) {
            return this;
        }
        return new SingleUnum(value > 0 ? Math.nextUp(value) : Math.nextDown(value));
    }

    public static float getUpperBound(final float value) {
        if (isExact(value) | Float.isNaN(value)) {
            return value;
        }
        return value > 0 ? Math.nextUp(value) : Math.nextDown(value);
    }

    @Override
    public SingleUnum intervalSize() {
        final float size = intervalSize(value);
        return size == 0 ? ZERO : SingleUnum.valueOf(size);
    }

    public static float intervalSize(final float value) {
        if (isExact(value)) {
            return 0.0f;
        }
        if (Float.isNaN(value)) {
            return Singles.signedNaN(value);
        }
        if (value >= 0) {
            return nextUp(value) - value;
        } else {
            return value - nextDown(value);
        }
    }

    @Override
    public int compareTo(final SingleUnum other) {
        return compare(value, other.value);
    }

    public static int compare(final float a, final float b) {
        if (a < b) return -1;
        if (a > b) return 1;
        if (a == b) return 0;

        //at least one is NaN
        final int araw = Float.floatToRawIntBits(a);
        final int braw = Float.floatToRawIntBits(b);
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
    public SingleUnum min(final SingleUnum other) {
        return compareTo(other) <= 0 ? this : other;
    }

    public static float min(final float a, final float b) {
        return compare(a, b) <= 0 ? a : b;
    }

    @Override
    public SingleUnum max(final SingleUnum other) {
        return compareTo(other) >= 0 ? this : other;
    }

    public static float max(final float a, final float b) {
        return compare(a, b) >= 0 ? a : b;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (getClass() == obj.getClass()) {
            return 0 == compareTo((SingleUnum)obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(value);
    }

    public static String toString(final float value) {
        if (isExact(value)) {
            return String.valueOf(value);
        }
        if (Float.isNaN(value)) {
            return Float.floatToRawIntBits(value) >= 0 ? "qNaN" : "sNaN";
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
            System.out.println(SingleUnum.valueOf(i));
        }
        System.out.println("*** exactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(SingleUnum.exactValueOf(i));
        }
        System.out.println("*** inexactValueOf");
        for (int i = -10; i < 10; i++) {
            System.out.println(SingleUnum.inexactValueOf(i));
        }
        System.out.println("*** valueOf/nextDown");
        for (int i = -10; i < 10; i++) {
            System.out.println(SingleUnum.valueOf(Math.nextDown((float)i)));
        }
        System.out.println("*** specials");
        System.out.println(+0.0);
        System.out.println(-0.0);
        System.out.println(SingleUnum.valueOf(Float.POSITIVE_INFINITY));
        System.out.println(SingleUnum.valueOf(Float.NEGATIVE_INFINITY));
        System.out.println(SingleUnum.QNAN);
        System.out.println(SingleUnum.SNAN);
        System.out.println(FACTORY.empty());
        System.out.println(FACTORY.ubound(ONE, TWO));
        System.out.println(FACTORY.ubound(ONE.nextUp(), TWO.nextDown()));
        System.out.println(FACTORY.ubound(ONE, TWO.nextDown()));
        System.out.println(FACTORY.ubound(ONE.nextUp(), TWO));
        final SingleUnum neg1 = valueOf(-1.0f);
        final SingleUnum neg2 = valueOf(-2.0f);
        System.out.println(FACTORY.ubound(neg2, neg1));
        System.out.println(FACTORY.ubound(neg2.nextDown(), neg1.nextUp()));
        System.out.println(FACTORY.ubound(neg2, neg1.nextUp()));
        System.out.println(FACTORY.ubound(neg2.nextDown(), neg1));
    }
}
