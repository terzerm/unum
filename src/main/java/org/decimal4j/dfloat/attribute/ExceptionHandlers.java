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
package org.decimal4j.dfloat.attribute;

import org.decimal4j.dfloat.api.FloatMath;

public enum ExceptionHandlers implements ExceptionHandler {
    Default {
        @Override
        public long handleException(String operation, long a, long b, long result, Flag flag, Flag otherFlag, Attributes attributes) {
            return result;
        }
    },
    /**
     * Specifiable for any exception arising from multiplication or division operations: like {@link SubstituteHandler}, but
     * replaces the default result of such an exceptional operation with |x| and, if |x| is not a NaN, obtaining the sign
     * bit from the XOR of the signs of the operands.
     */
    SubstituteXor {
        @Override
        public long handleException(String operation, long a, long b, long result, Flag flag, Flag otherFlag, Attributes attributes) {
            return Float.isNaN(result) ? result : FloatMath.copySign(result, a ^ b);
        }
    },
    /**
     * When underflow is signaled because a tiny non-zero result is detected, the default result is replaced with a zero of
     * the same sign or a minimum normal rounded result of the same sign, the underflow flag is raised and the inexact
     * exception is signalled.
     * <p>
     * When NearestTiesToEven, NearestTiesToAway, or the RoundTowardZero rounding direction is applicable,
     * the rounded result magnitude is zero.
     * <p>
     * When the RoundTowardPositive rounding direction is applicable, the rounded
     * result magnitude is the minimum normal magnitude for positive tiny results, and zero for negative tiny results.
     * <p>
     * When the RoundTowardNegative rounding direction is applicable, the rounded result magnitude is the minimum normal
     * magnitude for negative tiny results, and zero for positive tiny results. This attribute has no effect on the
     * interpretation of subnormal operands.
     */
    AbruptUnderflow {
        @Override
        public long handleException(String operation, long a, long b, long result, Flag flag, Flag otherFlag, Attributes attributes) {
            if (flag == Flag.Underflow) {
                switch (attributes.getDecimalRoundingDirection()) {
                    case NearestTiesToEven://fallthrough
                    case NearestTiesToAway://fallthrough
                    case TowardZero:
                        return FloatMath.copySign(FloatMath.ZERO, result);
                    case TowardPositive:
                        return FloatMath.isSignMinus(result) ? FloatMath.copySign(FloatMath.ZERO, result) : FloatMath.MIN_NORMAL;
                    case TowardNegative:
                        return FloatMath.isSignMinus(result) ? FloatMath.copySign(FloatMath.MIN_NORMAL, result) : FloatMath.ZERO;
                    default:
                        //should not happen
                        throw new IllegalArgumentException("Unknown rounding direction: " + attributes.getDecimalRoundingDirection());
                }
            }
            return result;
        }
    },
    ThrowException {
        @Override
        public long handleException(final String operation,
                                    final long a, final long b, final long result,
                                    final Flag flag, final Flag otherFlag,
                                    final Attributes attributes) {
            throw new RaisedFlagException(operation, a, b, result, flag, otherFlag, isConstant(attributes) ? attributes : DynamicAttributes.copyOf(attributes));
        }
    };

    private static boolean isConstant(final Attributes attributes) {
        return attributes == Attributes.DEFAULT || attributes instanceof RoundingDirection;
    }
}
