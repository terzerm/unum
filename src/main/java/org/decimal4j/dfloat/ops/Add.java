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

import org.decimal4j.dfloat.attribute.Attributes;
import org.decimal4j.dfloat.attribute.Flags;
import org.decimal4j.dfloat.attribute.RoundingDirection;
import org.decimal4j.dfloat.encode.Decimal64;
import org.decimal4j.dfloat.encode.Dpd;
import org.decimal4j.dfloat.signal.Signal;

import static org.decimal4j.dfloat.ops.Sign.copySignToPositive;

public final class Add {

    private static final String ADD = "add";
    private static final String SUB = "subtract";

    static enum OpMode{
        Add {
            @Override
            final long a(final long a, final long b) {
                return a;
            }

            @Override
            final long b(long a, long b) {
                return b;
            }

            @Override
            final OpMode flip() {
                return AddFlipped;
            }
        },
        AddFlipped {
            @Override
            final long a(final long a, final long b) {
                return b;
            }

            @Override
            final long b(long a, long b) {
                return a;
            }

            @Override
            final OpMode flip() {
                return Add;
            }
        },
        Subtract {
            @Override
            final long a(final long a, final long b) {
                return a;
            }

            @Override
            final long b(long a, long b) {
                return Sign.flipSign(b);
            }

            @Override
            final OpMode flip() {
                return SubtractFlipped;
            }
        },
        SubtractFlipped {
            @Override
            final long a(final long a, final long b) {
                return Sign.flipSign(b);
            }

            @Override
            final long b(long a, long b) {
                return a;
            }

            @Override
            final OpMode flip() {
                return Subtract;
            }
        };
        final String operation() {return this == Add | this == AddFlipped ? ADD : SUB;}
        abstract long a(long a, long b);
        abstract long b(long a, long b);
        abstract OpMode flip();
    }

    public static long add(final long a, final long b) {
        return add(a, b, Attributes.DEFAULT, OpMode.Add);
    }
    public static long add(final long a, final long b, final RoundingDirection roundingDirection) {
        return add(a, b, roundingDirection.asAttributes(), OpMode.Add);
    }
    public static long add(final long a, final long b, final Attributes attributes) {
        return add(a, b, attributes, OpMode.Add);
    }
    static long add(final long a, final long b, final Attributes attributes, OpMode opMode) {
        Flags.resetFlags(attributes.getResetMode());
        if (Decimal64.isFinite(a) & Decimal64.isFinite(b)) {
            return addFinite(a, b, attributes, opMode);
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
                if ((a ^ b) < 0) {
                    return Signal.invalidOperation(opMode.operation(), a, b, Decimal64.NAN, attributes);
                }
                return copySignToPositive(Decimal64.INF, a);
            }
            return copySignToPositive(Decimal64.INF, a);
        }
        return copySignToPositive(Decimal64.INF, b);

    }
    private static final long addFinite(final long a, final long b,
                                        final Attributes attributes,
                                        OpMode opMode) {
        final int msdA = Decimal64.getCombinationMSD(a);
        final int msdB = Decimal64.getCombinationMSD(b);
        final int expA = Decimal64.getExponent(a);
        final int expB = Decimal64.getExponent(b);
        if (expA == expB) {
            return addFiniteSameExponent(msdA, a, msdB, b, expA, attributes, opMode);
        }
        if (expA > expB) {
            return addFiniteDifferentExponent(msdA, a, expA, msdB, b, expB, attributes, opMode);
        } else {
            return addFiniteDifferentExponent(msdB, b, expB, msdA, a, expA, attributes, opMode.flip());
        }
    }

    private static long addFiniteSameExponent(final int msdA, final long a,
                                              final int msdB, final long b,
                                              final int exp, final Attributes attributes,
                                              OpMode opMode) {
        if ((a ^ b) >= 0) {
            //a and b have same sign
            return addFiniteSameExponentAndSign(msdA, a, msdB, b, exp, attributes, opMode);
        }
        //exactly one is negative
        if (compareMantissa(msdA, a, msdB, b) >= 0) {
            return addFiniteSameExponentOppositeSign(msdA, a, msdB, b, exp);
        } else {
            return addFiniteSameExponentOppositeSign(msdB, b, msdA, a, exp);
        }
    }

    private static long addFiniteSameExponentAndSign(final int msdA, final long a,
                                                     final int msdB, final long b,
                                                     final int exp, final Attributes attributes,
                                                     OpMode opMode) {
        final long sum10to50 = Dpd.add(a, b);
        final int sumMSD = msdA + msdB + (int) (sum10to50 >>> 51);
        if (sumMSD <= 9) {
            return Decimal64.encode(a & Decimal64.SIGN_BIT_MASK, exp, sumMSD, sum10to50);
        }
        //mantissa overflow
        final int loMSD = sumMSD - 10;
        final int hiMSD = 1;// |hi|lo| = |1|x| becomes 1 after shift right
        if (exp < Decimal64.MAX_EXPONENT) {
            final long rsh = Dpd.shiftRight(loMSD, sum10to50);
            final int mod = Dpd.mod10(sum10to50);
            if (mod != 0) {
                //inexact result
                final RoundingDirection roundingDirection = attributes.getDecimalRoundingDirection();
                final long sgn = a & Decimal64.SIGN_BIT_MASK;
                if (roundingDirection.isRoundingIncrementPossible(sgn)) {
                    final int lsd10 = Dpd.mod10(rsh);
                    final Remainder remainder = Remainder.ofDigit(mod);
                    final int inc = roundingDirection.getRoundingIncrement(sgn, lsd10, remainder);
                    if (inc > 0) {
                        return incRoundingAndSignalInexact(sgn, exp + 1, hiMSD, rsh, opMode, a, b, attributes);
                    }
                }
                final long result = Decimal64.encode(sgn, exp + 1, hiMSD, rsh);
                return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
            }
            //still exact after shift right
            return Decimal64.encode(a, exp + 1, hiMSD, rsh);
        }
        //exponent overflow
        final long result = attributes.getDecimalRoundingDirection().roundOverflow(a);
        return Signal.overflow(opMode.operation(), a, b, result, attributes);
    }

    //PRECONDITION: |a| >= |b|
    private static long addFiniteSameExponentOppositeSign(final int msdA, final long a, final int msdB, final long b, final int exp) {
        final long sign = a & Decimal64.SIGN_BIT_MASK;
        final long sub10to50 = Dpd.sub(a, b);
        final int subMSD = msdA - msdB - (int) (sub10to50 >>> 51);
        return Decimal64.encode(sign, exp, subMSD, sub10to50);
    }

    private static int compareMantissa(final int msdA, final long a, final int msdB, final long b) {
        final int cmp = Long.compare(msdA, msdB);
        return cmp != 0 ? cmp : Dpd.compare(a, b);
    }

    //PRECONDITION: expA > expB
    private static long addFiniteDifferentExponent(final int msdA, final long a, final int expA,
                                                   final int msdB, final long b, final int expB,
                                                   final Attributes attributes, final OpMode opMode) {
        if ((a ^ b) >= 0) {
            //a and b have same sign
            return addFiniteDifferentExponentSameSign(msdA, a, expA, msdB, b, expB, attributes, opMode);
        } else {
            return addFiniteDifferentExponentAndSign(msdA, a, expA, msdB, b, expB, attributes, opMode);
        }
    }
    //PRECONDITION: expA > expB
    private static long addFiniteDifferentExponentSameSign(final int msdA, final long a, final int expA,
                                                           final int msdB, final long b, final int expB,
                                                           final Attributes attributes, final OpMode opMode) {
        final int expDiff = expA - expB;
        if (expDiff < 16) {
            //mantissa have some overlap: | msdA | a1 | a0+[msdB,b1] | b0 |
            if (expDiff == 15) {
                //only msdB overlaps with a: | msdA | a1 | a0+msdB | b |
                final long sum = Dpd.inc(a, msdB);
                final long s = sum & Decimal64.COEFF_CONT_MASK;
                final int msdS = msdA + (int) (s >>> 51);
                if (a <= 9 & msdA <= 9) {
                    //we're done, the |a1| part is empty and |a0+msdB| did not overflow
                    return Decimal64.encode(a, expB, msdS, s);
                }
                //exact result: |msdA|s|b|  (<= 31 bits)
                if (msdA > 0) {
                    //exponent: expA
                    if (b != 0) {
                        //inexact result
                        final RoundingDirection roundingDirection = attributes.getDecimalRoundingDirection();
                        final long sgn = a & Decimal64.SIGN_BIT_MASK;
                        if (roundingDirection.isRoundingIncrementPossible(sgn)) {
                            final Remainder remainder = Dpd.remainderOfPow10(b, 15);
                            final int lsd = Dpd.mod10(s);
                            final int inc = roundingDirection.getRoundingIncrement(sgn, lsd, remainder);
                            if (inc > 0) {
                                return incRoundingAndSignalInexact(sgn, expA, msdA, s, opMode, a, b, attributes);
                            }
                        }
                        final long result = Decimal64.encode(sgn, expA, msdA, s);
                        return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
                    }
                    //exact result
                    return Decimal64.encode(a, expA, msdA, s);
                }
                //exact result: |s|b|  (<= 30 bits)
                //we must try to approach expB as much as possible
                if (s <= 9) {
                    //we're done. exact result is |s|b| with expB
                    return Decimal64.encode(a, expB, (int)s, b);
                }
                final int shift = Math.min(15, 1 + Dpd.numberOfLeadingZeros(s));
                final int msd = Dpd.dpdToCharDigit(s, 15 - shift) - '0';
                final long hi = Dpd.shiftLeft(s, shift);
                final long lo = Dpd.shiftRight(b, 15-shift);
                final long dpd = Dpd.add(hi, lo);//no overflow possible (it is logically an or, not an add)
                final Remainder remainder = Dpd.remainderOfPow10(b, 15 - shift);
                if (remainder != Remainder.ZERO) {
                    //inexact result
                    final RoundingDirection roundingDirection = attributes.getDecimalRoundingDirection();
                    final long sgn = a & Decimal64.SIGN_BIT_MASK;
                    if (roundingDirection.isRoundingIncrementPossible(sgn)) {
                        final int lsd = Dpd.mod10(dpd);
                        final int inc = roundingDirection.getRoundingIncrement(sgn, lsd, remainder);
                        if (inc > 0) {
                            return incRoundingAndSignalInexact(sgn, expA - shift, msd, dpd, opMode, a, b, attributes);
                        }
                    }
                    final long result = Decimal64.encode(sgn, expA - shift, msd, dpd);
                    return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
                }
                //exact result
                return Decimal64.encode(a, expA - shift, msd, dpd);
            }
            //msdB plus at least 1 digit of b overlaps with a: | msdA | a1 | a0+[msdB,b1] | b0 |
            final long hiB = Dpd.shiftRight(msdB, b, expDiff);
            final long dpd = Dpd.add(a, hiB);
            final int msd = msdA + (int)(dpd >>> 51);


            return Decimal64.NAN;//FIXME
        } else {
            //mantissa have no overlap:   | a | ... | b |
            //--> result = a + round(b)
            final long s;
            final int msdS, expS, expD, modS;
            final int nlzA = msdA > 0 ? 0 : 1 + Dpd.numberOfLeadingZeros(a);
            if (nlzA > 0) {
                //we can shift |a| somewhat to the left to approach preferred exponent expB
                if (nlzA == 15) {
                    //a must be zero, return b
                    return Decimal64.encode(b & Decimal64.SIGN_BIT_MASK, expB, msdB, b);
                }
                final int shift = Math.min(nlzA, expDiff);
                final long shifted = Dpd.shiftLeft(a, shift);
                s = shift & Decimal64.COEFF_CONT_MASK;
                msdS = (int)(shifted >>> 51);
                expS = expA - shift;
                expD = expDiff - shift;
                modS = 0;
            } else {
                s = a;
                msdS = msdA;
                expS = expA;
                expD = expDiff;
                modS = Dpd.mod10(s);
            }
            final long sign = a & Decimal64.SIGN_BIT_MASK;
            if (!isZero(msdB, b)) {
                final RoundingDirection roundingDirection = attributes.getDecimalRoundingDirection();
                if (roundingDirection.isRoundingIncrementPossible(sign)) {
                    final Remainder remainder = expD > 16 | msdB < 5 ? Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF : msdB > 5 | (msdB == 5 & b != 0) ? Remainder.GREATER_THAN_HALF : Remainder.EQUAL_TO_HALF;
                    final int inc = roundingDirection.getRoundingIncrement(sign, modS, remainder);
                    if (inc > 0) {
                        return incRoundingAndSignalInexact(sign, expS, msdS, s, opMode, a, b, attributes);
                    }
                }
                final long result = Decimal64.encode(sign, expA, msdA, a);
                return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
            }
            return Decimal64.encode(sign, expA, msdA, a);
        }
    }

    //PRECONDITION: expA > expB
    private static long addFiniteDifferentExponentAndSign(final int msdA, final long a, final int expA,
                                                          final int msdB, final long b, final int expB,
                                                          final Attributes attributes, final OpMode opMode) {
        return Decimal64.NAN;//FIXME
    }

    private static long incRoundingAndSignalInexact(final long sign, final int exp, final int msd, final long dpd,
                                                    final OpMode opMode, final long a, final long b, final Attributes attributes) {
        final long incremented = Dpd.inc(dpd);
        final long dpdI = incremented & Decimal64.COEFF_CONT_MASK;
        final int msdI = msd + (int) (incremented >>> 51);
        if (msdI <= 9) {
            final long result = Decimal64.encode(sign, exp, msdI, dpdI);
            return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
        }
        //mantissa overflow
        if (exp < Decimal64.MAX_EXPONENT) {
            //after increment, it must be 10.0000, shift right becomes 1.0000
            final long result = Decimal64.encode(sign, exp + 1, 1, 0);
            return Signal.inexact(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
        }
        //exponent overflow ==> Infinity
        final long result = attributes.getDecimalRoundingDirection().roundOverflow(sign);
        return Signal.overflow(opMode.operation(), opMode.a(a, b), opMode.b(a, b), result, attributes);
    }

    private static boolean isZero(final int msb, final long dpd) {
        return msb == 0 & Dpd.isZero(dpd);
    }
}