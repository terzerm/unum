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

import org.decimal4j.dfloat.api.RoundingDirection;
import org.decimal4j.dfloat.encode.Decimal64;
import org.decimal4j.dfloat.encode.Dpd;
import org.decimal4j.dfloat.signal.Signal;

import static org.decimal4j.dfloat.ops.Sign.copySign;
import static org.decimal4j.dfloat.ops.Sign.copySignToPositive;

public final class Add {

    public static long add(long a, long b) {
        if (Decimal64.isFinite(a) & Decimal64.isFinite(b)) {
            return addFinite(a, b, RoundingDirection.DEFAULT);
        }
        //at least one is NaN or Infinite
        if (Decimal64.isNaN(a)) {
            return copySignToPositive(Decimal64.NAN | Dpd.canonicalize(a), a);
        }
        if (Decimal64.isNaN(b)) {
            return copySignToPositive(Decimal64.NAN | Dpd.canonicalize(b), b);
        }
        //at least one Infinite
        if (Decimal64.isInfinite(a)) {
            if (Decimal64.isInfinite(b)) {
                return (a ^ b) < 0 ? Signal.invalidOperation() : copySignToPositive(Decimal64.INF, a);
            }
            return copySignToPositive(Decimal64.INF, a);
        }
        return copySignToPositive(Decimal64.INF, b);

    }
    private static final long addFinite(final long a, final long b, final RoundingDirection roundingDirection) {
        final int expA = Decimal64.getExponent(a);
        final int expB = Decimal64.getExponent(b);
        if (expA == expB) {
            if ((a ^ b) >= 0) {
                //a and b have same sign
                final long sum10to50 = Dpd.add(a, b);
                final int sumMSD = Decimal64.getCombinationMSD(a) + Decimal64.getCombinationMSD(b) + (int) (sum10to50 >>> 51);
                if (sumMSD <= 9) {
                    return Decimal64.encode(a & Decimal64.SIGN_BIT_MASK, expA, sumMSD, sum10to50);
                }
                //mantissa overflow
                if (expA < Decimal64.MAX_EXPONENT) {
                    final long shifted = Dpd.shiftRight(sum10to50);
                    final int mod10 = Dpd.mod10(sum10to50);
                    final int lsd10 = Dpd.mod10(shifted);
                    final Remainder remainder = Remainder.ofDigit(mod10);
                    final int inc = roundingDirection.getRoundingIncrement(Sign.sign(a), lsd10, remainder);
                    final long rounded = inc > 0 ? Dpd.inc(shifted) : shifted;//cannot overflow
                    return Decimal64.encode(a & Decimal64.SIGN_BIT_MASK, expA + 1, sumMSD - 10, rounded);
                }
                //exponent overflow ==> Infinity
                return Sign.copySign(Decimal64.INF, a);
            } else {
                //exactly one is negative, subtract smaller from larger magnitude
                final long msbA = Decimal64.getCombinationMSD(a);
                final long msbB = Decimal64.getCombinationMSD(b);
                long cmp = Long.compare(msbA, msbB);
                if (cmp == 0) {
                    cmp = Dpd.compare(a, b);
                }
                final long sub10to50;
                final int subMSD;
                final long sign;
                if (cmp >= 0) {
                    //a >= b
                    sign = a & Decimal64.SIGN_BIT_MASK;
                    sub10to50 = Dpd.sub(a, b);
                    subMSD = Decimal64.getCombinationMSD(a) - Decimal64.getCombinationMSD(b) - (int) (sub10to50 >>> 51);
                } else {
                    //a < b
                    sign = b & Decimal64.SIGN_BIT_MASK;
                    sub10to50 = Dpd.sub(b, a);
                    subMSD = Decimal64.getCombinationMSD(b) - Decimal64.getCombinationMSD(a) - (int) (sub10to50 >>> 51);
                }
                return Decimal64.encode(sign, expA, subMSD, sub10to50);
            }
        }
        else return Decimal64.NAN;//FIXME
    }
}