/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.dfloat.api;

public enum ValueClass {
    SignalingNaN,
    QuietNaN,
    NegativeInfinity,
    NegativeNormal,
    NegativeSubnormal,
    NegativeZero,
    PositiveZero,
    PositiveSubnormal,
    PositiveNormal,
    PositiveInfinity;

    public final boolean isNaN() {
        return this == SignalingNaN | this == QuietNaN;
    }
    public final boolean isInfinite() {
        return this == NegativeInfinity | this == PositiveInfinity;
    }
    public final boolean isNormal() {
        return this == NegativeNormal | this == PositiveNormal;
    }
    public final boolean isSubnormal() {
        return this == NegativeSubnormal | this == PositiveSubnormal;
    }
    public final boolean isZero() {
        return this == NegativeZero | this == PositiveZero;
    }
    public final boolean isFinite() {
        return !isNaN() & !isInfinite();
    }
    public final boolean isPositive() {
        return this == PositiveNormal | this == PositiveZero | this == PositiveSubnormal | this == PositiveInfinity;
    }
    public final boolean isNegative() {
        return this == NegativeNormal | this == NegativeZero | this == NegativeSubnormal | this == NegativeInfinity;
    }

    public static ValueClass classFor(final long a) {
        if (FloatMath.isFinite(a)) {
            if (FloatMath.isNormal(a)) {
                return FloatMath.isSignMinus(a) ? NegativeNormal : PositiveNormal;
            }
            if (FloatMath.isZero(a)) {
                return FloatMath.isSignMinus(a) ? NegativeZero : PositiveZero;
            }
            return FloatMath.isSignMinus(a) ? NegativeSubnormal : PositiveSubnormal;
        }
        if (FloatMath.isInfinite(a)) {
            return FloatMath.isSignMinus(a) ? NegativeInfinity : PositiveInfinity;
        }
        return FloatMath.isSignalingNaN(a) ? SignalingNaN : QuietNaN;
    }
}
