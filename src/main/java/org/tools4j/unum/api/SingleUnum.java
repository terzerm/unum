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
 * A Universal number backed by a single precision float.
 */
public class SingleUnum extends AbstractUnum<SingleUnum> implements Serializable {

    private static final int UBIT_MASK = 0x00000001;

    public static final SingleUnum ZERO  = new SingleUnum(0f);
    public static final SingleUnum ONE   = new SingleUnum(1f);
    public static final SingleUnum TWO   = new SingleUnum(2f);
    public static final SingleUnum TEN   = new SingleUnum(10f);
    public static final SingleUnum POSITIVE_INFINITY = new SingleUnum(Float.POSITIVE_INFINITY);
    public static final SingleUnum NEGATIVE_INFINITY = new SingleUnum(Float.NEGATIVE_INFINITY);
    public static final SingleUnum QNAN  = new SingleUnum(Singles.QNAN);
    public static final SingleUnum SNAN  = new SingleUnum(Singles.SNAN);

    public static final Ubound<SingleUnum> UBOUND_ZERO = Ubound.create(ZERO);
    public static final Ubound<SingleUnum> UBOUND_ONE = Ubound.create(ONE);
    public static final Ubound<SingleUnum> UBOUND_QNAN = Ubound.create(QNAN);
    public static final Ubound<SingleUnum> UBOUND_SNAN = Ubound.create(SNAN);

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

    public static final Factory<Ubound<SingleUnum>> UBOUND_FACTORY = new Factory<Ubound<SingleUnum>>() {
        @Override
        public Ubound<SingleUnum> qNaN() {
            return UBOUND_QNAN;
        }
        @Override
        public Ubound<SingleUnum> sNaN() {
            return UBOUND_SNAN;
        }
        @Override
        public Ubound<SingleUnum> zero() {
            return UBOUND_ZERO;
        }
        @Override
        public Ubound<SingleUnum> one() {
            return UBOUND_ONE;
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
    public Factory<Ubound<SingleUnum>> getUboundFactory() {
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
        return value < 0f;
    }

    @Override
    public boolean isPositive() {
        return value > 0f;
    }

    @Override
    public boolean isSignNegative() {
        return isSignNegative(value);
    }

    public static boolean isSignNegative(final float value) {
        return value < 0f || Float.floatToRawIntBits(value) < 0;
    }

    @Override
    public boolean isZero() {
        return value == 0f;
    }

    @Override
    public boolean isNonNegative() {
        return value >= 0f;
    }

    @Override
    public boolean isNonPositive() {
        return value <= 0f;
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
        return new SingleUnum(value > 0f ? Math.nextDown(value) : Math.nextUp(value));
    }

    public static float getLowerBound(final float value) {
        if (isExact(value) | Float.isNaN(value)) {
            return value;
        }
        return value > 0f ? Math.nextDown(value) : Math.nextUp(value);
    }

    @Override
    public SingleUnum getUpperBound() {
        if (isExact() | isNaN()) {
            return this;
        }
        return new SingleUnum(value > 0f ? Math.nextUp(value) : Math.nextDown(value));
    }

    public static float getUpperBound(final float value) {
        if (isExact(value) | Float.isNaN(value)) {
            return value;
        }
        return value > 0f ? Math.nextUp(value) : Math.nextDown(value);
    }

    @Override
    public SingleUnum intervalWidth() {
        final float size = intervalWidth(value);
        return size == 0 ? ZERO : SingleUnum.valueOf(size);
    }

    public static float intervalWidth(final float value) {
        if (isExact(value)) {
            return Float.isFinite(value) ? 0f : Float.POSITIVE_INFINITY;
        }
        if (Float.isNaN(value)) {
            return Singles.signedNaN(value);
        }
        return nextUp(value) - nextDown(value);
    }

    @Override
    public SingleUnum add(SingleUnum other) {
        return SingleUnum.valueOf(add(value, other.value));
    }

    public static float add(final float a, final float b) {
        if (isExact(a) & isExact(b)) {
            final float s = a + b;
            if (s - a == b & s - b == a) {
                return s;//also ok if s is inexact
            }
            if (Float.isInfinite(s)) {
                if (Float.isFinite(a) | Float.isFinite(b)) {
                    return s;
                }
                //both infinite
                return Math.signum(a) == Math.signum(b) ? s : Singles.QNAN;
            }
            if (isExact(s)) {
                if (s - a <= b & s - b <= a) return nextUp(s);
                if (s - a >= b & s - b >= a) return nextDown(s);
                //FIXME is this possible?
                throw new InternalError();
            }
            //result must be between s-ulp and s+ulp, hence we're done
            return s;
        }
        return Singles.QNAN;
    }

    @Override
    public SingleUnum subtract(SingleUnum other) {
        return SingleUnum.valueOf(subtract(value, other.value));
    }

    public static float subtract(final float a, final float b) {
        if (isExact(a) & isExact(b)) {
            final float d = a - b;
            if (d - a == -b & d + b == a) {
                return d;//also ok if d is inexact
            }
            if (Float.isInfinite(d)) {
                if (Float.isFinite(a) | Float.isFinite(b)) {
                    return d;
                }
                //both infinite
                return Math.signum(a) != Math.signum(b) ? d : Singles.QNAN;
            }
            if (isExact(d)) {
                if (d - a <= -b & d + b <= a) return nextUp(d);
                if (d - a >= -b & d + b >= a) return nextDown(d);
                //FIXME is this possible?
                throw new InternalError();
            }
            //result must be between s-ulp and s+ulp, hence we're done
            return d;
        }
        return Singles.QNAN;
    }

    @Override
    public SingleUnum multiply(SingleUnum other) {
        return SingleUnum.valueOf(multiply(value, other.value));
    }

    public static float multiply(final float a, final float b) {
        if (isExact(a) & isExact(b)) {
            if (a == 0f | b == 0f) {
                return 0f;
            }
            final float p = a * b;
            if (p / a == b & p / b == a) {
                return p;//also ok if d is inexact
            }
            if (Float.isInfinite(p)) {
                return p;
            }
            if (isExact(p)) {
                if (p / a <= b & p / b <= a) return nextUp(p);
                if (p / a >= b & p / b >= a) return nextDown(p);
                //FIXME is this possible?
                throw new InternalError();
            }
            //result must be between s-ulp and s+ulp, hence we're done
            return p;
        }
        return Singles.QNAN;
    }

    @Override
    public SingleUnum divide(SingleUnum other) {
        throw new RuntimeException("not implemented");//FIXME implement
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
    public SingleUnum negate() {
        return new SingleUnum(-value);
    }

    @Override
    public SingleUnum abs() {
        return value >= 0 ? this : value < 0 ? negate() : this /*NaN*/;
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
        if (value >= 0f) {
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
        System.out.println("*** a + a");
        for (int i = -10; i < 10; i++) {
            System.out.println(SingleUnum.valueOf(i).add(SingleUnum.valueOf(i)));
            final float f1 = Math.nextDown(i);
            System.out.println(SingleUnum.valueOf(f1) + " + " + i + " = " + SingleUnum.valueOf(f1).add(SingleUnum.valueOf(i)));
            final float f2 = Math.nextDown(Math.nextDown(i));
            System.out.println(SingleUnum.valueOf(f2) + " + " + i + " = " + SingleUnum.valueOf(f2).add(SingleUnum.valueOf(i)));
            System.out.println(SingleUnum.valueOf(f2) + "*2 = " + SingleUnum.valueOf(f2).add(SingleUnum.valueOf(f2)));
        }
        System.out.println("*** a + a");
        for (int i = -10; i < 10; i++) {
        }
        System.out.println("*** specials");
        System.out.println(+0.0);
        System.out.println(-0.0);
        System.out.println(SingleUnum.valueOf(Float.POSITIVE_INFINITY));
        System.out.println(SingleUnum.valueOf(Float.NEGATIVE_INFINITY));
        System.out.println(SingleUnum.QNAN);
        System.out.println(SingleUnum.SNAN);
        System.out.println(UBOUND_FACTORY.qNaN());
        System.out.println(Ubound.create(ONE, TWO));
        System.out.println(Ubound.create(ONE.nextUp(), TWO.nextDown()));
        System.out.println(Ubound.create(ONE, TWO.nextDown()));
        System.out.println(Ubound.create(ONE.nextUp(), TWO));
        System.out.println(Ubound.create(ONE, ONE));
        System.out.println(Ubound.create(ONE.nextUp(), ONE.nextUp()));
        System.out.println("inter:" + Ubound.create(ONE, TWO).intersect(Ubound.create(ZERO, TWO)));
        System.out.println("inter:" + Ubound.create(ZERO, ONE.nextUp()).intersect(Ubound.create(ONE, ONE.nextUp().nextUp())));
        System.out.println("span:" + Ubound.create(ONE, TWO).span(Ubound.create(ZERO, TWO)));
        System.out.println("span:" + Ubound.create(ZERO, ONE.nextUp()).span(Ubound.create(ONE, ONE.nextUp().nextUp())));
        final SingleUnum neg1 = valueOf(-1.0f);
        final SingleUnum neg2 = valueOf(-2.0f);
        System.out.println(Ubound.create(neg2, neg1));
        System.out.println(Ubound.create(neg2.nextDown(), neg1.nextUp()));
        System.out.println(Ubound.create(neg2, neg1.nextUp()));
        System.out.println(Ubound.create(neg2.nextDown(), neg1));
        System.out.println(Ubound.create(neg1, neg1));
        System.out.println(Ubound.create(neg1.nextUp(), neg1.nextUp()));
        System.out.println("w: " + neg1.intervalWidth());
        System.out.println("w: " + neg1.nextUp().intervalWidth());
        System.out.println("w: " + neg1.nextDown().intervalWidth());
        System.out.println("w: " + POSITIVE_INFINITY.intervalWidth());
        System.out.println("w: " + NEGATIVE_INFINITY.intervalWidth());
//        System.out.println("w:" + Ubound.create(ONE, TWO).width());
//        System.out.println("w:" + Ubound.create(ONE, TWO.nextUp()).width());
//        System.out.println("w:" + Ubound.create(ONE.nextDown(), TWO).width());
//        System.out.println("w:" + Ubound.create(ONE.nextDown(), TWO.nextUp()).width());
    }
}
