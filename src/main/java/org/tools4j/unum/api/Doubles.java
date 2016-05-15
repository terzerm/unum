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
 * Created by terz on 9/05/2016.
 */
public class Doubles {

    public static final double QNAN = Double.NaN;
    public static final double SNAN = Double.longBitsToDouble(Double.doubleToRawLongBits(Double.NaN) | Long.MIN_VALUE);

    public static final double signedNaN(final double sign) {
        return sign < 0 || Double.doubleToRawLongBits(sign) < 0 ? SNAN : QNAN;
    }

    public static final boolean isQuietNaN(final double value) {
        return Double.isNaN(value) && Double.doubleToRawLongBits(value) >= 0;
    }

    public static final boolean isSignalingNaN(final double value) {
        return Double.isNaN(value) && Double.doubleToRawLongBits(value) < 0;
    }
}
