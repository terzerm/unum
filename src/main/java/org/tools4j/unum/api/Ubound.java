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

/**
 * An interval from one to another {@link Unum}. The {@link #getLowerBound() lower} and {@link #getUpperBound() upper}
 * bound unums can be inclusive or exclusive; exact and inexact unums determine inclusive and exclusive bounderies,
 * respectively.
 */
public interface Ubound<U extends Unum<U>> {
    enum Boundary {
        /** An closed/closed interval such as [2, 3] which includes both endpoints 2 and 3.*/
        CLOSED_CLOSED,
        /** An open/open interval such as (2, 3) which includes neither of the endpoints of 2 or 3.*/
        OPEN_OPEN,
        /** An open/closed interval such as (2, 3] which doesn't include endpoint 2 but includes 3.*/
        OPEN_CLOSED,
        /** An open/closed interval such as [2, 3) which includes endpoint 2 but not 3.*/
        CLOSED_OPEN;
    }
    enum Overlap {
        /*order of constants matters!*/

        /**
         * Empty because of a NaN.
         * <p>
         * An example is the intersection of (2, 3] and (NaN).
         */
        EMPTY,
        /**
         * Empty because two ubounds are apart and non-touching
         * <p>
         * An example is the intersection of (2, 3] and (4, 5).
         */
        APART,
        /**
         * Empty, but the two ubounds are touching, which means one boundary is common but open for at least one of the
         * ubounds.
         * <p>
         * An example is the intersection of (2, 3] and (3, 4).
         */
        NEARLY_TOUCHING,
        /**
         * Touching with a single-point overlap. The touching point is a closed end for both ubounds.
         * <p>
         * An example is the intersection of (2, 3] and [3, 4); note that the intersection of (2, 3] and [3] is
         * CONTAINING, not TOUCHING.
         */
        TOUCHING,
        /**
         * Two overlapping ubounds with more than one common points.
         * <p>
         * An example is the intersection of (2, 3] and (2.5, 4).
         */
        OVERLAPPING,
        /**
         * One ubound contains the other.
         * <p>
         * Examples are the intersection of:
         * <ul>
         *     <li>(2, 3] and [2.5, 2.75]</li>
         *     <li>(2, 3] and (2, 3)</li>
         *     <li>(2, 3] and [3]</li>
         * </ul>
         */
        CONTAINING,
        /**
         * The two ubounds are equal and non-empty.
         * <p>
         * An example is the intersection of (2, 3) and (2, 3); note that the intersection of (NaN) and (NaN) is EMPTY,
         * not EQUAL.
         */
        EQUAL;

        /**
         * True if the intersection of two ubounds is empty, which is true if an a (NaN) ubound is involved or if the
         * intersection does not overlap.
         * @return true if two bounds are nowhere equal, which includes the intersection with an empty (NaN) ubound.
         */
        public final boolean isNowhereEqual() {
            return ordinal() < TOUCHING.ordinal();
        }

        /**
         * True if the intersection of two ubounds is non-empty.
         * @return true if two bounds are somewhere equal, which means have at least one point in common.
         */
        public final boolean isSomewhereEqual() {
            return ordinal() >= TOUCHING.ordinal();
        }
    }
    U getLowerBound();
    U getUpperBound();
    Ubound<U> width();
    Boundary boundary();
    Overlap overlap(Ubound<U> other);
    boolean isNowhereEqualTo(Ubound<U> other);
    boolean isSomewhereEqualTo(Ubound<U> other);
    boolean isEverywhereEqualTo(Ubound<U> other);
    Ubound<U> intersect(Ubound<U> with);
    Ubound<U> span(Ubound<U> with);
//    Ubound<U> add(Ubound<U> summand);
//    Ubound<U> subtract(Ubound<U> summand);

    default Factory<Ubound<U>> getFactory() {
        return getLowerBound().getUboundFactory();
    }
    default boolean isNaN() {
        return getLowerBound().isNaN() || getUpperBound().isNaN();
    }
    default boolean isSinglePoint() {
        return getLowerBound().isExact() && 0 == getLowerBound().compareTo(getUpperBound());
    }
    default boolean isLowerClosed() {
        return getLowerBound().isExact();
    }
    default boolean isLowerOpen() {
        return getLowerBound().isInexact();
    }
    default boolean isUpperClosed() {
        return getUpperBound().isExact();
    }
    default boolean isUpperOpen() {
        return getUpperBound().isInexact();
    }
    default boolean isOpen() {
        return isLowerOpen() || isUpperOpen();
    }
    default boolean isClosed() {
        return isLowerClosed() && isUpperClosed();
    }

    default boolean isLessThan(final Ubound<U> other) {
        if (isNaN()) return false;
        final int cmp = getUpperBound().compareTo(other.getLowerBound());
        return cmp < 0 || (cmp == 0 && (isUpperOpen() | other.isLowerOpen()));
    }
    default boolean isLessThanOrEqualTo(final Ubound<U> other) {
        if (isNaN()) return false;
        return getUpperBound().compareTo(other.getLowerBound()) <= 0;
    }
    default boolean isGreaterThan(final Ubound<U> other) {
        if (isNaN()) return false;
        final int cmp = getLowerBound().compareTo(other.getUpperBound());
        return cmp > 0 || (cmp == 0 && (isLowerOpen() | other.isUpperOpen()));
    }
    default boolean isGreaterThanOrEqualTo(final Ubound<U> other) {
        if (isNaN()) return false;
        return getLowerBound().compareTo(other.getUpperBound()) >= 0;
    }
    default boolean isNegative() {
        return getUpperBound().isNegative() || (getUpperBound().isZero() & isUpperOpen());
    }
    default boolean isPositive() {
        return getLowerBound().isPositive() || (getLowerBound().isZero() & isLowerOpen());
    }
    default boolean isZero() {
        return getLowerBound().isZero() & getUpperBound().isZero();
    }
    default boolean isNonNegative() {
        return getLowerBound().isNonNegative();
    }
    default boolean isNonPositive() {
        return getUpperBound().isNonPositive();
    }

    static <U extends Unum<U>> Ubound<U> create(final U unum) {
        return create(unum, unum);
    }
    static <U extends Unum<U>> Ubound<U> create(final U lower, final U upper) {
        return new DefaultUbound<U>(lower, upper);
    }
}
