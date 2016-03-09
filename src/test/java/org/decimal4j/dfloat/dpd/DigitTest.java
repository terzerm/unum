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
package org.decimal4j.dfloat.dpd;

import org.decimal4j.dfloat.encode.Decimal64;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class DigitTest {

    private static final Random RND = new Random();

    @Test
    public void decletToDigit() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int val = Declet.dpdToInt(dpd);
            final int exp0 = (val / 100) % 10;
            final int exp1 = (val / 10) % 10;
            final int exp2 = val % 10;
            assertEquals("decletToIntDigit(" + dpd + ", 0)", exp0, Digit.decletToIntDigit(dpd, 0));
            assertEquals("decletToIntDigit(" + dpd + ", 1)", exp1, Digit.decletToIntDigit(dpd, 1));
            assertEquals("decletToIntDigit(" + dpd + ", 2)", exp2, Digit.decletToIntDigit(dpd, 2));
            assertEquals("decletToCharDigit(" + dpd + ", 0)", (char)(exp0 + '0'), Digit.decletToCharDigit(dpd, 0));
            assertEquals("decletToCharDigit(" + dpd + ", 1)", (char)(exp1 + '0'), Digit.decletToCharDigit(dpd, 1));
            assertEquals("decletToCharDigit(" + dpd + ", 2)", (char)(exp2 + '0'), Digit.decletToCharDigit(dpd, 2));
        }
    }

    @Test
    public void numberOfLeadingZeros() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int val = Declet.dpdToInt(dpd);
            final int nlz = val == 0 ? 3 : 3 - String.valueOf(val).length();
            assertEquals("numberOfLeadingZeros(" + dpd + ")", nlz, Digit.numberOfLeadingZeros(dpd));
        }
    }

    @Test
    public void numberOfTrailingZeros() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int val = Declet.dpdToInt(dpd);
            int ntz;
            if (val == 0) {
                ntz = 3;
            } else {
                final String s = String.valueOf(val);
                ntz = s.charAt(s.length() - 1) == '0' ? 1 : 0;
                ntz += ntz == 1 && s.length() > 1 && s.charAt(s.length() - 2) == '0' ? 1 : 0;
                ntz += ntz == 2 && s.length() > 2 && s.charAt(s.length() - 3) == '0' ? 1 : 0;
            }
            assertEquals("numberOfTrailingZeros(" + dpd + ")", ntz, Digit.numberOfTrailingZeros(dpd));
        }
    }

    @Test
    public void dpdToCharDigit() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final long fiveDeclets = RND.nextLong() & Decimal64.COEFF_CONT_MASK;
            final int val = Declet.dpdToInt(dpd);
            final int[] exp = {(val / 100) % 10, (val / 10) % 10, val % 10};
            for (int shift = 0; shift < 50; shift += 10) {
                final long dpd5 = fiveDeclets & (~(0x3ffL << shift)) | (((long)dpd) << shift);
                for (int i = 0; i < 3; i++) {
                    final int index = 12 - ((shift/10)*3) + i;
                    assertEquals("dpdToCharDigit(" + dpd5 + ", " + index + ")", (char)(exp[i] + '0'), Digit.dpdToCharDigit(dpd5, index));
                }
            }
        }
    }

    @Test
    public void digitsToDpd() {
        for (int d0 = 0; d0 < 10; d0++) {
            for (int d1 = 0; d1 < 10; d1++) {
                for (int d2 = 0; d2 < 10; d2++) {
                    final int dpd = Declet.intToDpd(100*d0 + 10*d1 + d2);
                    assertEquals("intDigitsToDpd(" + d0 + ", " + d1 + ", " + d2 + ")", dpd, Digit.intDigitsToDpd(d0, d1, d2));
                    assertEquals("charDigitsToDpd(" + d0 + ", " + d1 + ", " + d2 + ")", dpd, Digit.charDigitsToDpd((char)(d0 + '0'), (char)(d1 + '0'), (char)(d2 + '0')));
                }
            }
        }
    }
}