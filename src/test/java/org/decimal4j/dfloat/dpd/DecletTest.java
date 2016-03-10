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

import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class DecletTest {

    static final int[] NON_CANONICALS = new int[] {366, 367, 382, 383, 494, 495, 510, 511, 622, 623, 638, 639, 750, 751, 766, 767, 878, 879, 894, 895, 1006, 1007, 1022, 1023};
    static final int[] REP_CANONICALS = new int[] {110, 111, 126, 127, 238, 239, 254, 255, 110, 111, 126, 127, 238, 239, 254, 255, 110, 111, 126, 127, 238, 239, 254, 255};

    @Test
    public void isCanonical() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final boolean expected = Arrays.binarySearch(NON_CANONICALS, dpd) < 0;
            assertEquals("isCanonical(" + dpd + ")", expected, Declet.isCanonical(dpd));
        }
    }

    @Test
    public void canonicalize() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int i = Arrays.binarySearch(NON_CANONICALS, dpd);
            final int expected = i < 0 ? dpd : REP_CANONICALS[i];
            assertEquals("canonicalize(" + dpd + ")", expected, Declet.canonicalize(dpd));
        }
    }

    @Test
    public void dpdToInt() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int i = Arrays.binarySearch(NON_CANONICALS, dpd);
            final int expected = i < 0 ? dpd : REP_CANONICALS[i];
            assertEquals("intToDpd(dpdToInt(" + dpd + "))", expected, Declet.intToDpd(Declet.dpdToInt(dpd)));
        }
    }

    @Test
    public void intToDpd() {
        for (int ival = 0; ival < 1000; ival++) {
            final int actual = Declet.intToDpd(ival);

            final int expected = intDigitsToDpd(ival / 100, (ival / 10) % 10, ival % 10);
            assertEquals("intToDpd(" + ival + ")", expected, actual);
            assertEquals("dpdToInt(intToDpd(" + ival + "))", ival, Declet.dpdToInt(actual));
        }
    }

    @Test
    public void inc() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            for (int inc = 0; inc < 1000; inc++) {
                final int actual = Declet.inc(dpd, inc);

                final int val = Declet.dpdToInt(dpd);
                final int sum = val + inc;
                final int expected = sum < 1000 ? Declet.intToDpd(sum) : (1<<10) | Declet.intToDpd(sum - 1000);
                assertEquals("inc(" + dpd + ", " + inc + ")", expected, actual);
            }
        }
    }

    @Test
    public void dec() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            for (int dec = 0; dec < 1000; dec++) {
                final int actual = Declet.dec(dpd, dec);

                final int val = Declet.dpdToInt(dpd);
                final int diff = val - dec;
                final int expected = diff >= 0 ? Declet.intToDpd(diff) : (1<<10) | Declet.intToDpd(diff + 1000);
                assertEquals("dec(" + dpd + ", " + dec + ")", expected, actual);
            }
        }
    }

    @Test
    public void add() {
        for (int dpd1 = 0; dpd1 < 1024; dpd1++) {
            for (int dpd2 = 0; dpd2 < 1024; dpd2++) {
                for (int carry = 0; carry <= 1; carry++) {
                    final int actual = Declet.add(dpd1, dpd2, carry);

                    final int val1 = Declet.dpdToInt(dpd1);
                    final int val2 = Declet.dpdToInt(dpd2);
                    final int diff = val1 + val2 + carry;
                    final int expected = diff < 1000 ? Declet.intToDpd(diff) : (1<<10) | Declet.intToDpd(diff - 1000);
                    assertEquals("add(" + dpd1 + ", " + dpd2 + ", " + carry + ")", expected, actual);
                }
            }
        }
    }

    @Test
    public void sub() {
        for (int dpd1 = 0; dpd1 < 1024; dpd1++) {
            for (int dpd2 = 0; dpd2 < 1024; dpd2++) {
                for (int carry = 0; carry <= 1; carry++) {
                    final int actual = Declet.sub(dpd1, dpd2, carry);

                    final int val1 = Declet.dpdToInt(dpd1);
                    final int val2 = Declet.dpdToInt(dpd2);
                    final int diff = val1 - val2 - carry;
                    final int expected = diff >= 0 ? Declet.intToDpd(diff) : (1<<10) | Declet.intToDpd(diff + 1000);
                    assertEquals("sub(" + dpd1 + ", " + dpd2 + ", " + carry + ")", expected, actual);
                }
            }
        }
    }

    @Test
    public void compare() {
        for (int dpd1 = 0; dpd1 < 1024; dpd1++) {
            final int val1 = Declet.dpdToInt(dpd1);
            for (int dpd2 = 0; dpd2 < 1024; dpd2++) {
                final int val2 = Declet.dpdToInt(dpd2);
                final int actual = Declet.compare(dpd1, dpd2);
                final int expected = Integer.compare(val1, val2);

                assertEquals("compare(" + dpd1 + ", " + dpd2 + ")", Integer.signum(expected), Integer.signum(actual));
            }
        }
    }

    @Test
    public void numberOfLeadingZeros() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int val = Declet.dpdToInt(dpd);
            final int nlz = val == 0 ? 3 : 3 - String.valueOf(val).length();
            assertEquals("numberOfLeadingZeros(" + dpd + ")", nlz, Declet.numberOfLeadingZeros(dpd));
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
            assertEquals("numberOfTrailingZeros(" + dpd + ")", ntz, Declet.numberOfTrailingZeros(dpd));
        }
    }

    @Test(expected = RuntimeException.class)
    public void newInstance() throws Throwable {
        Instance.notAllowed(Declet.class);
    }

    /**
     * Converts 3 integer digits digits to DPD.
     *
     * @param d2
     *            most significant digit, 0-9
     * @param d1
     *            middle significant digit, 0-9
     * @param d0
     *            least significant digit, 0-9
     * @return 10 bit dpd encoding of the same 3 decimal digits
     */
    private static int intDigitsToDpd(final int d2, final int d1, final int d0) {
        final int s2 = d2 >> 3;// & 0x1
        final int s1 = d1 >> 3;// & 0x1
        final int s0 = d0 >> 3;// & 0x1
        final int p0 = (~s2) & (~s1) & (~s0);
        final int p1 = (~s2) & (~s1) & s0;
        final int p2 = (~s2) & s1 & (~s0);
        final int p3 = s2 & (~s1) & (~s0);
        final int p4 = s2 & s1 & (~s0);
        final int p5 = s2 & (~s1) & s0;
        final int p6 = (~s2) & s1 & s0;
        final int p7 = s2 & s1 & s0;
        final int a = (d2 >> 2) & 0x1;
        final int b = (d2 >> 1) & 0x1;
        final int c = d2 & 0x1;
        final int d = (d1 >> 2) & 0x1;
        final int e = (d1 >> 1) & 0x1;
        final int f = d1 & 0x1;
        final int g = (d0 >> 2) & 0x1;
        final int h = (d0 >> 1) & 0x1;
        final int i = d0 & 0x1;
        //
        final int b0 = i;
        final int b1 = (p0 & h) | s1 | (s0 & s2);
        final int b2 = (p0 & g) | s2 | (s0 & s1);
        final int b3 = ~p0;
        final int b4 = f;
        final int b5 = ((p0 | p1 | p3) & e) | (p2 & h) | p5 | p7;
        final int b6 = ((p0 | p1 | p3) & d) | (p2 & g) | p6 | p7;
        final int b7 = c;
        final int b8 = ((p0 | p1 | p2 | p6) & b) | ((p3 | p4) & h) | (p5 & e);
        final int b9 = ((p0 | p1 | p2 | p6) & a) | ((p3 | p4) & g) | (p5 & d);
        return (b9 << 9) | (b8 << 8) | (b7 << 7) | (b6 << 6) | (b5 << 5) | (b4 << 4) | (b3 << 3) | (b2 << 2) | (b1 << 1)
                | b0;
    }
}