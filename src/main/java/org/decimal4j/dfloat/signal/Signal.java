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
package org.decimal4j.dfloat.signal;

import org.decimal4j.dfloat.attribute.*;
import org.decimal4j.dfloat.encode.Decimal64;

public class Signal {

    public static final long invalidOperation(final String operation,
                                              final long a, final long b, final long result,
                                              final Attributes attributes) {
        return signal(operation, a, b, result, Flag.InvalidOperation, null, attributes);
    }

    public static final long divisionByZero(final String operation,
                                            final long a, final long b, final long result,
                                            final Attributes attributes) {
        return signal(operation, a, b, result, Flag.DivisionByZero, null, attributes);
    }

    public static final long overflow(final String operation,
                                      final long a, final long b, final long result,
                                      final Attributes attributes) {
        return signal(operation, a, b, result, Flag.Overflow, Flag.Inexact, attributes);
    }

    private Signal() {
        throw new IllegalStateException("No Flag for you!");
    }

    private static final long signal(final String operation,
                                     final long a, final long b, final long result,
                                     final Flag flag, final Flag otherFlag,
                                     final Attributes attributes) {
        final Flag raisedFlag, raisedOtherFlag;
        raisedFlag = raiseFlag(flag, attributes.getFlagMode(flag));
        if (otherFlag != null) {
            raisedOtherFlag = raiseFlag(otherFlag, attributes.getFlagMode(flag));
        } else {
            raisedOtherFlag = null;
        }
        if (raisedFlag != null) {
            return attributes.getExceptionHandler().handleException(operation, a, b, result, raisedFlag, raisedOtherFlag, attributes);
        }
        if (raisedOtherFlag != null) {
            return attributes.getExceptionHandler().handleException(operation, a, b, result, raisedOtherFlag, null, attributes);
        }
        return result;
    }
    private static final Flag raiseFlag(final Flag flag, final FlagMode flagMode) {
        return flagMode != FlagMode.RaiseNoFlag ? flag : null;
    }
}
