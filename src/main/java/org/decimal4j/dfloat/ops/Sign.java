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
package org.decimal4j.dfloat.ops;

import org.decimal4j.dfloat.encode.Decimal64;

import static org.decimal4j.dfloat.encode.Decimal64.isNaN;
import static org.decimal4j.dfloat.encode.Decimal64.isZero;

public final class Sign {
    public static long copySign(final long a, final long b) {
        return copySignToPositive(clearSign(a), b);
    }
    public static long clearSign(final long a) {
        return a & (~Decimal64.SIGN_BIT_MASK);
    }
    public static long flipSign(final long a) {
        return a ^ Decimal64.SIGN_BIT_MASK;
    }
    public static boolean isSignMinus(final long a) {
        return a < 0;
    }
    public static double signum(final long a) {
        if (isNaN(a)) return Double.NaN;
        return isZero(a) ? 0 : isSignMinus(a) ? -1 : 1;
    }
    static long copySignToPositive(final long a, final long b) {
        return a | (b & Decimal64.SIGN_BIT_MASK);
    }
}
