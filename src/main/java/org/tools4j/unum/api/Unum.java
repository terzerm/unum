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
 * An universial number.
 */
public interface Unum<U extends Unum<U>> extends Comparable<U> {

    Factory<U> getFactory();
    Factory<Ubound<U>> getUboundFactory();
    boolean isNaN();
    boolean isInfinite();
    boolean isFinite();
    boolean isExact();
    boolean isInexact();
    boolean isNegative();
    boolean isPositive();
    boolean isNonNegative();
    boolean isNonPositive();
    boolean isSignNegative();
    boolean isZero();

    U getLowerBound();
    U getUpperBound();
    U nextDown();
    U nextUp();
    U intervalWidth();

    U add(U other);
    U subtract(U other);
    U multiply(U other);
    U divide(U other);

    float floatValue();
    double doubleValue();
    int intValue();
    long longValue();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    int compareTo(U other);

    U min(U other);
    U max(U other);

    U negate();
    U abs();

    @Override
    String toString();
}
