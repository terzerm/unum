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

import org.decimal4j.dfloat.encode.Decimal64;

public class Signal {

    public static final long INVALID_OPERATION = Decimal64.NAN | 0x1;
    public static final long DIVISION_BY_ZERO = Decimal64.NAN | 0x2;
    public static final long OVERFLOW = Decimal64.NAN | 0x4;
    public static final long UNDERFLOW = Decimal64.NAN | 0x8;
    public static final long INEXACT = Decimal64.NAN | 0x10;

    public static final long invalidOperation() {
        return Decimal64.NAN | INVALID_OPERATION;
    }

    public static final long divisionByZero() {
        return Decimal64.NAN | DIVISION_BY_ZERO;
    }

    private Signal() {
        throw new IllegalStateException("No Signal for you!");
    }
}
