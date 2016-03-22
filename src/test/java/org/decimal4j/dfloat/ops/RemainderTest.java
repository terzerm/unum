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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemainderTest {

    private static final long[] POW10 = new long[] {
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
            1000000000,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L,
            1000000000000000000L
    };

    @Test
    public void isZero() throws Exception {
        for (final Remainder rem : Remainder.values()) {
            assertEquals(rem + ".isZero()", rem == Remainder.ZERO, rem.isZero());
        }
    }

    @Test
    public void isGreaterThanZero() throws Exception {
        for (final Remainder rem : Remainder.values()) {
            assertEquals(rem + ".isGreaterThanZero()", rem != Remainder.ZERO, rem.isGreaterThanZero());
        }
    }

    @Test
    public void isLessThanHalf() throws Exception {
        for (final Remainder rem : Remainder.values()) {
            assertEquals(rem + ".isLessThanHalf()", rem == Remainder.ZERO | rem == Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, rem.isLessThanHalf());
        }
    }

    @Test
    public void isEqualToHalf() throws Exception {
        for (final Remainder rem : Remainder.values()) {
            assertEquals(rem + ".isEqualToHalf()", rem == Remainder.EQUAL_TO_HALF, rem.isEqualToHalf());
        }
    }

    @Test
    public void isGreaterThanHalf() throws Exception {
        for (final Remainder rem : Remainder.values()) {
            assertEquals(rem + ".isGreaterThanHalf()", rem == Remainder.GREATER_THAN_HALF, rem.isGreaterThanHalf());
        }
    }

    @Test
    public void ofDigit() throws Exception {
        for (int digit = 0; digit <= 9; digit++) {
            switch (digit) {
                case 0:
                    assertEquals("ofDigit(" + digit + ")", Remainder.ZERO, Remainder.ofDigit(digit));
                    break;
                case 1://fallthrough
                case 2://fallthrough
                case 3://fallthrough
                case 4:
                    assertEquals("ofDigit(" + digit + ")", Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, Remainder.ofDigit(digit));
                    break;
                case 5:
                    assertEquals("ofDigit(" + digit + ")", Remainder.EQUAL_TO_HALF, Remainder.ofDigit(digit));
                    break;
                default:
                    assertEquals("ofDigit(" + digit + ")", Remainder.GREATER_THAN_HALF, Remainder.ofDigit(digit));
                    break;
            }
        }
    }

    @Test
    public void of() throws Exception {
        for (int msd = 0; msd <= 9; msd++) {
            switch (msd) {
                case 0:
                    assertEquals("of(" + msd + ", 0)", Remainder.ZERO, Remainder.of(msd, 0));
                    break;
                case 1://fallthrough
                case 2://fallthrough
                case 3://fallthrough
                case 4:
                    assertEquals("of(" + msd + ", 0)", Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, Remainder.of(msd, 0));
                    break;
                case 5:
                    assertEquals("of(" + msd + ", 0)", Remainder.EQUAL_TO_HALF, Remainder.of(msd, 0));
                    break;
                default:
                    assertEquals("of(" + msd + ", 0)", Remainder.GREATER_THAN_HALF, Remainder.of(msd, 0));
                    break;
            }
        }
        for (int msd = 0; msd <= 9; msd++) {
            final long rem = 12345L;
            switch (msd) {
                case 0:
                    assertEquals("of(" + msd + ", <>0)", Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, Remainder.of(msd, rem));
                    break;
                case 1://fallthrough
                case 2://fallthrough
                case 3://fallthrough
                case 4:
                    assertEquals("of(" + msd + ", <>0)", Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, Remainder.of(msd, rem));
                    break;
                default:
                    assertEquals("of(" + msd + ", <>0)", Remainder.GREATER_THAN_HALF, Remainder.of(msd, rem));
                    break;
            }
        }
    }

    @Test
    public void ofPow10() throws Exception {
        for (int n = 1; n < POW10.length; n++) {
            final long pow10 = POW10[n];
            for (final long val : new long[] {0, 1, pow10/2 - 1, pow10/2, pow10/2 + 1, pow10 - 1}) {
                final Remainder exp = val == 0 ? Remainder.ZERO : val == pow10 / 2 ? Remainder.EQUAL_TO_HALF : val < pow10 / 2 ? Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF : Remainder.GREATER_THAN_HALF;
                assertEquals("ofPow10(" + val + ", " + n + ")", exp, Remainder.ofPow10(val, n));
            }
        }
        assertEquals("ofPow10(" + Long.MAX_VALUE + ", " + 19 + ")", Remainder.GREATER_THAN_HALF, Remainder.ofPow10(Long.MAX_VALUE, 19));
        assertEquals("ofPow10(" + Long.MAX_VALUE + ", " + 20 + ")", Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF, Remainder.ofPow10(Long.MAX_VALUE, 20));
    }
}