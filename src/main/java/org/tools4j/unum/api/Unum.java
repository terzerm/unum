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
 * An universial number.
 */
public interface Unum<F> extends Serializable, Comparable<Unum<F>> {

    boolean isNaN();
    boolean isInfinite();
    boolean isFinite();
    boolean isExact();
    boolean isInexact();
    boolean isNegative();
    boolean isPositive();
    boolean isZero();

    int getExponent();
    int getExponentSize();
    F getFraction();
    int getFractionSize();

    Unum<F> getLowerBound();
    Unum<F> getUpperBound();

    @Override
    int compareTo(Unum<F> o);

    @Override
    boolean equals(Object o);

    @Override
    String toString();
}
