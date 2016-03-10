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
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class DpdTest {

    private static final Random RND = new Random();

    @Test
    public void isCanonical() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final boolean expected = Declet.isCanonical(dpd);
            long dpd5 = 0;
            for (int pos = 0; pos < 5; pos++) {
                final long single5 = ((long)dpd) << (pos*10);
                dpd5 |= single5;
                assertEquals("isCanonical(" + single5 + ")", expected, Dpd.isCanonical(single5));
                assertEquals("isCanonical(" + dpd5 + ")", expected, Dpd.isCanonical(dpd5));
                for (final int nonCanonic : DecletTest.NON_CANONICALS) {
                    for (int pos2 = 0; pos2 < 5; pos2++) {
                        if (pos != pos2) {
                            final long non5 = single5 | (((long)nonCanonic) << (pos2*10));
                            assertEquals("isCanonical(" + non5 + ")", false, Dpd.isCanonical(non5));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void canonicalize() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int can = Declet.canonicalize(dpd);
            long dpd5 = 0, can5 = 0;
            for (int pos = 0; pos < 5; pos++) {
                final long single5 = ((long)dpd) << (pos*10);
                final long canoni5 = ((long)can) << (pos*10);
                dpd5 |= single5;
                can5 |= canoni5;
                assertEquals("canonicalize(" + single5 + ")", canoni5, Dpd.canonicalize(single5));
                assertEquals("canonicalize(" + dpd5 + ")", can5, Dpd.canonicalize(dpd5));
                for (int i = 0; i < DecletTest.NON_CANONICALS.length; i++) {
                    final int non = DecletTest.NON_CANONICALS[i];
                    final int rep = DecletTest.REP_CANONICALS[i];
                    for (int pos2 = 0; pos2 < 5; pos2++) {
                        if (pos != pos2) {
                            final long non5 = single5 | (((long)non) << (pos2*10));
                            final long yes5 = canoni5 | (((long)rep) << (pos2*10));
                            assertEquals("canonicalize(" + non5 + ")", yes5, Dpd.canonicalize(non5));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void inc() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            for (int inc = 0; inc < 1000; inc++) {
                final int val = Declet.dpdToInt(dpd);
                final int sum = val + inc;
                final int expected = sum < 1000 ? Declet.intToDpd(sum) : (1<<10) | Declet.intToDpd(sum - 1000);
                assertEquals("inc(" + dpd + ", " + inc + ")", expected, Dpd.inc(dpd, inc));
                if (inc == 1) {
                    assertEquals("inc(" + dpd + ")", expected, Dpd.inc(dpd));
                }
            }
        }
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 0) + ")", intsToDpd(0, 0, 0, 0, 1), Dpd.inc(intsToDpd(0, 0, 0, 0, 0)));
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 1) + ")", intsToDpd(0, 0, 0, 0, 2), Dpd.inc(intsToDpd(0, 0, 0, 0, 1)));
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 998) + ")", intsToDpd(0, 0, 0, 0, 999), Dpd.inc(intsToDpd(0, 0, 0, 0, 998)));
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 999) + ")", intsToDpd(0, 0, 0, 1, 0), Dpd.inc(intsToDpd(0, 0, 0, 0, 999)));
        assertEquals("inc(" + intsToDpd(1, 2, 3, 4, 567) + ")", intsToDpd(1, 2, 3, 4, 568), Dpd.inc(intsToDpd(1, 2, 3, 4, 567)));
        assertEquals("inc(" + intsToDpd(1, 2, 3, 4, 999) + ")", intsToDpd(1, 2, 3, 5, 0), Dpd.inc(intsToDpd(1, 2, 3, 4, 999)));
        assertEquals("inc(" + intsToDpd(1, 2, 3, 999, 999) + ")", intsToDpd(1, 2, 4, 0, 0), Dpd.inc(intsToDpd(1, 2, 3, 999, 999)));
        assertEquals("inc(" + intsToDpd(1, 2, 999, 999, 999) + ")", intsToDpd(1, 3, 0, 0, 0), Dpd.inc(intsToDpd(1, 2, 999, 999, 999)));
        assertEquals("inc(" + intsToDpd(1, 999, 999, 999, 999) + ")", intsToDpd(2, 0, 0, 0, 0), Dpd.inc(intsToDpd(1, 999, 999, 999, 999)));
        assertEquals("inc(" + intsToDpd(999, 999, 999, 999, 999) + ")", (1L<<50) | intsToDpd(0, 0, 0, 0, 0), Dpd.inc(intsToDpd(999, 999, 999, 999, 999)));
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 0) + ", 999)", intsToDpd(0, 0, 0, 0, 999), Dpd.inc(intsToDpd(0, 0, 0, 0, 0), 999));
        assertEquals("inc(" + intsToDpd(0, 0, 0, 0, 1) + ", 999)", intsToDpd(0, 0, 0, 1, 0), Dpd.inc(intsToDpd(0, 0, 0, 0, 1), 999));
        assertEquals("inc(" + intsToDpd(123, 456, 789, 998, 999) + ", 999)", intsToDpd(123, 456, 789, 999, 998), Dpd.inc(intsToDpd(123, 456, 789, 998, 999), 999));
        assertEquals("inc(" + intsToDpd(456, 789, 998, 999, 999) + ", 999)", intsToDpd(456, 789, 999, 0, 998), Dpd.inc(intsToDpd(456, 789, 998, 999, 999), 999));
        assertEquals("inc(" + intsToDpd(789, 998, 999, 999, 999) + ", 999)", intsToDpd(789, 999, 0, 0, 998), Dpd.inc(intsToDpd(789, 998, 999, 999, 999), 999));
        assertEquals("inc(" + intsToDpd(998, 999, 999, 999, 999) + ", 999)", intsToDpd(999, 0, 0, 0, 998), Dpd.inc(intsToDpd(998, 999, 999, 999, 999), 999));
        assertEquals("inc(" + intsToDpd(999, 999, 999, 999, 999) + ", 999)", (1L<<50) | intsToDpd(0, 0, 0, 0, 998), Dpd.inc(intsToDpd(999, 999, 999, 999, 999), 999));
    }

    @Test
    public void dec() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            for (int dec = 0; dec < 1000; dec++) {
                final int val = Declet.dpdToInt(dpd);
                final int sum = val - dec;
                final long expected = sum >= 0 ? Declet.intToDpd(sum) : (1L<<50) | intsToDpd(999, 999, 999, 999, sum + 1000);
                assertEquals("dec(" + dpd + ", " + dec + ")", expected, Dpd.dec(dpd, dec));
                if (dec == 1) {
                    assertEquals("dec(" + dpd + ")", expected, Dpd.dec(dpd));
                }
            }
        }
        assertEquals("dec(" + intsToDpd(0, 0, 0, 0, 999) + ")", intsToDpd(0, 0, 0, 0, 998), Dpd.dec(intsToDpd(0, 0, 0, 0, 999)));
        assertEquals("dec(" + intsToDpd(0, 0, 0, 0, 2) + ")", intsToDpd(0, 0, 0, 0, 1), Dpd.dec(intsToDpd(0, 0, 0, 0, 2)));
        assertEquals("dec(" + intsToDpd(0, 0, 0, 0, 1) + ")", intsToDpd(0, 0, 0, 0, 0), Dpd.dec(intsToDpd(0, 0, 0, 0, 1)));
        assertEquals("dec(" + intsToDpd(4, 3, 2, 1, 0) + ")", intsToDpd(4, 3, 2, 0, 999), Dpd.dec(intsToDpd(4, 3, 2, 1, 0)));
        assertEquals("dec(" + intsToDpd(3, 2, 1, 0, 0) + ")", intsToDpd(3, 2, 0, 999, 999), Dpd.dec(intsToDpd(3, 2, 1, 0, 0)));
        assertEquals("dec(" + intsToDpd(2, 1, 0, 0, 0) + ")", intsToDpd(2, 0, 999, 999, 999), Dpd.dec(intsToDpd(2, 1, 0, 0, 0)));
        assertEquals("dec(" + intsToDpd(1, 0, 0, 0, 0) + ")", intsToDpd(0, 999, 999, 999, 999), Dpd.dec(intsToDpd(1, 0, 0, 0, 0)));
        assertEquals("dec(" + intsToDpd(0, 0, 0, 0, 0) + ")", (1L<<50) | intsToDpd(999, 999, 999, 999, 999), Dpd.dec(intsToDpd(0, 0, 0, 0, 0)));
        assertEquals("dec(" + intsToDpd(555, 444, 333, 222, 111) + ", 223)", intsToDpd(555, 444, 333, 221, 888), Dpd.dec(intsToDpd(555, 444, 333, 222, 111), 223));
        assertEquals("dec(" + intsToDpd(444, 333, 222, 000, 111) + ", 223)", intsToDpd(555, 444, 332, 999, 888), Dpd.dec(intsToDpd(555, 444, 333, 000, 111), 223));
        assertEquals("dec(" + intsToDpd(444, 333, 000, 000, 111) + ", 223)", intsToDpd(555, 443, 999, 999, 888), Dpd.dec(intsToDpd(555, 444, 000, 000, 111), 223));
        assertEquals("dec(" + intsToDpd(444, 000, 000, 000, 111) + ", 223)", intsToDpd(554, 999, 999, 999, 888), Dpd.dec(intsToDpd(555, 000, 000, 000, 111), 223));
        assertEquals("dec(" + intsToDpd(000, 000, 000, 000, 111) + ", 223)", (1L<<50) | intsToDpd(999, 999, 999, 999, 888), Dpd.dec(intsToDpd(000, 000, 000, 000, 111), 223));
    }

    @Test
    public void add() {
        for (int i = 0; i < 100000; i++) {
            final long a = RandomDpd.randomDpd();
            final long b = RandomDpd.randomDpd();
            final long s = dpdToLong(a) + dpdToLong(b);
            final long exp = s < 1000000000000000L ? longToDpd(s) : (1L<<50) | longToDpd(s - 1000000000000000L);
            assertEquals("add(" + a + ", " + b + ")", exp, Dpd.add(a, b));
        }
        assertEquals("add(" + longToDpd(999999999999999L) + ", " + longToDpd(1L) + ")",
                (1L<<50) | longToDpd(0),
                Dpd.add(longToDpd(999999999999999L), longToDpd(1)));
        assertEquals("add(" + longToDpd(999999999999999L) + ", " + longToDpd(999999999999999L) + ")",
                (1L<<50) | longToDpd(999999999999998L),
                Dpd.add(longToDpd(999999999999999L), longToDpd(999999999999999L)));
    }

    @Test
    public void sub() {
        for (int i = 0; i < 100000; i++) {
            final long a = RandomDpd.randomDpd();
            final long b = RandomDpd.randomDpd();
            final long d = dpdToLong(a) - dpdToLong(b);
            final long exp = d >= 0 ? longToDpd(d) : (1L<<50) | longToDpd(d + 1000000000000000L);
            assertEquals("sub(" + a + ", " + b + ")", exp, Dpd.sub(a, b));
        }
        assertEquals("sub(" + longToDpd(1L) + ", " + longToDpd(2L) + ")",
                (1L<<50) | longToDpd(999999999999999L),
                Dpd.sub(longToDpd(1L), longToDpd(2L)));
        assertEquals("sub(" + longToDpd(1) + ", " + longToDpd(999999999999999L) + ")",
                (1L<<50) | longToDpd(2L),
                Dpd.sub(longToDpd(1), longToDpd(999999999999999L)));
        assertEquals("sub(" + longToDpd(345L) + ", " + longToDpd(123456789012345L) + ")",
                (1L<<50) | longToDpd(876543210988000L),
                Dpd.sub(longToDpd(345L), longToDpd(123456789012345L)));
    }

    @Test
    public void compare() {
        for (int i = 0; i < 100000; i++) {
            final long a = RandomDpd.randomDpd();
            final long b = RandomDpd.randomDpd();
            final long c = Long.compare(dpdToLong(a), dpdToLong(b));
            assertEquals("compare(" + a + ", " + b + ")", Long.signum(c), Long.signum(Dpd.compare(a, b)));
            assertEquals("compare(" + b + ", " + a + ")", -Long.signum(c), Long.signum(Dpd.compare(b, a)));
        }
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +0, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(1, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(1, 1, 1, 1, 2) + ")", -1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(1, 1, 1, 1, 2))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 2) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 2), intsToDpd(1, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(1, 1, 1, 3, 1) + ")", -1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(1, 1, 1, 3, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 3, 1) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 3, 1), intsToDpd(1, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(1, 1, 4, 1, 1) + ")", -1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(1, 1, 4, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 4, 1, 1) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +1, Long.signum(Dpd.compare(intsToDpd(1, 1, 4, 1, 1), intsToDpd(1, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(1, 5, 1, 1, 1) + ")", -1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(1, 5, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 5, 1, 1, 1) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +1, Long.signum(Dpd.compare(intsToDpd(1, 5, 1, 1, 1), intsToDpd(1, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(1, 1, 1, 1, 1) + ", " + intsToDpd(6, 1, 1, 1, 1) + ")", -1, Long.signum(Dpd.compare(intsToDpd(1, 1, 1, 1, 1), intsToDpd(6, 1, 1, 1, 1))));
        assertEquals("compare(" + intsToDpd(6, 1, 1, 1, 1) + ", " + intsToDpd(1, 1, 1, 1, 1) + ")", +1, Long.signum(Dpd.compare(intsToDpd(6, 1, 1, 1, 1), intsToDpd(1, 1, 1, 1, 1))));
    }

    @Test
    public void numberOfLeadingZeros() {
        for (int i = 0; i < 10000; i++) {
            for (int zeros = 0; zeros < 16; zeros++) {
                final String s = "000000000000000" + Math.abs(RND.nextLong());
                final long l = Long.valueOf("000000000000000".substring(0, zeros) + s.substring(s.length() - (15-zeros)));
                final int trueZeros = l == 0 ? 15 : 15 - String.valueOf(l).length();
                final long dpd = longToDpd(l);
                assertEquals("numberOfLeadingZeros(" + l + ")", trueZeros, Dpd.numberOfLeadingZeros(dpd));
            }
        }
    }

    @Test
    public void numberOfTrailingZeros() {
        for (int i = 0; i < 10000; i++) {
            for (int zeros = 0; zeros < 16; zeros++) {
                final String s = (Math.abs(RND.nextLong()) + "000000000000000");
                final long l = Long.valueOf(s.substring(s.length() - 15));
                final int trueZeros = l == 0 ? 15 : 15 - reverse(String.valueOf(l)).length();
                final long dpd = longToDpd(l);
                assertEquals("numberOfTrailingZeros(" + l + ")", trueZeros, Dpd.numberOfTrailingZeros(dpd));
            }
        }
    }

    @Test
    public void isZero() {
        assertEquals("isZero(" + 0 + ")", true, Dpd.isZero(0));
        for (int i = 0; i < 50; i++) {
            final long dpd = 1L << i;
            assertEquals("isZero(" + dpd + ")", false, Dpd.isZero(dpd));
        }
        for (int i = 50; i < 64; i++) {
            final long dpd = 1L << i;
            assertEquals("isZero(" + dpd + ")", true, Dpd.isZero(dpd));
        }
    }

    @Test(expected = RuntimeException.class)
    public void newInstance() throws Throwable {
        Instance.notAllowed(Dpd.class);
    }

    private static final long intsToDpd(final int int0, final int int1, final int int2, final int int3, final int int4) {
        return dpd(Declet.intToDpd(int0), Declet.intToDpd(int1), Declet.intToDpd(int2), Declet.intToDpd(int3), Declet.intToDpd(int4));
    }
    private static final long dpd(final int declet0, final int declet1, final int declet2, final int declet3, final int declet4) {
        return (((long)declet0) << 40) | (((long)declet1) << 30) | ((long)declet2 << 20) | (declet3 << 10) | declet4;
    }
    private static final long dpdToLong(final long a) {
        return 1000000000000L * Declet.dpdToInt((int)((a >>> 40) & 0x3ff)) +
                1000000000L * Declet.dpdToInt((int)((a >>> 30) & 0x3ff)) +
                1000000L * Declet.dpdToInt((int)((a >>> 20) & 0x3ff)) +
                1000L * Declet.dpdToInt((int)((a >>> 10) & 0x3ff)) +
                Declet.dpdToInt((int)(a & 0x3ff));
    }
    private static final long longToDpd(final long a) {
        return intsToDpd((int)((a / 1000000000000L) % 1000), (int)((a / 1000000000L) % 1000), (int)((a / 1000000L) % 1000), (int)((a / 1000L) % 1000), (int)(a % 1000));
    }
    private static final String reverse(final String s) {
        return new StringBuilder(s).reverse().toString();
    }
}