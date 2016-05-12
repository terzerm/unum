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

/**
 * An interval from one to another {@link Unum}. The {@link #getLowerBound() lower} and {@link #getUpperBound() upper}
 * bound unums can be inclusive or exclusive; exact and inexact unums determine inclusive and exclusive bounderies,
 * respectively.
 */
public interface Ubound<U extends Unum<U>> {
    enum Boundery {
        CLOSED_CLOSED,
        OPEN_OPEN,
        OPEN_CLOSED,
        CLOSED_OPEN;
    }
    enum Overlap {
        /*order of constants matters!*/
        EMPTY,
        APART,
        NEARLY_TOUCHING,
        TOUCHING,
        OVERLAPPING,
        CONTAINING,
        EQUAL;
        public final boolean isNowhereEqual() {
            return ordinal() < TOUCHING.ordinal();
        }
        public final boolean isSomewhereEqual() {
            return ordinal() >= TOUCHING.ordinal();
        }
    }
    U getLowerBound();
    U getUpperBound();
    U getIntervalWidth();
    Boundery getBoundery();
    Overlap getOverlap(Ubound<U> other);
    boolean isNowhereEqualTo(Ubound<U> other);
    boolean isSomewhereEqualTo(Ubound<U> other);
    boolean isEverywhereEqualTo(Ubound<U> other);
    Ubound<U> intersect(Ubound<U> with);
    Ubound<U> span(Ubound<U> with);

    default Factory<U> getFactory() {
        return getLowerBound().getFactory();
    }
    default boolean isEmpty() {
        return getLowerBound().isNaN() || getUpperBound().isNaN();
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

    static <U extends Unum<U>> Ubound<U> create(final U lower, final U upper) {
        return new DefaultUbound<U>(lower, upper);
    }
}
