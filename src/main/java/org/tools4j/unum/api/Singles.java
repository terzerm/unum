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
public class Singles {

    public static final float QNAN = Float.NaN;
    public static final float SNAN = Float.intBitsToFloat(Float.floatToRawIntBits(Float.NaN) | Integer.MIN_VALUE);

    public static final float signedNaN(final float sign) {
        return sign < 0 || Float.floatToRawIntBits(sign) < 0 ? SNAN : QNAN;
    }

    public static final boolean isQuietNaN(final float value) {
        return Float.isNaN(value) && Float.floatToRawIntBits(value) >= 0;
    }

    public static final boolean isSignalingNaN(final float value) {
        return Float.isNaN(value) && Float.floatToRawIntBits(value) < 0;
    }
}
