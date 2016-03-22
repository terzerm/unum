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

import org.decimal4j.dfloat.ops.Remainder;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RemTest {

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
            10000000000000000L};

    private static final Random RND = new Random();

    @Test
    public void mod10() {
        for (int dpd = 0; dpd < 1024; dpd++) {
            final int val = Declet.dpdToInt(dpd);
            final int exp = val % 10;
            final long ldpd = (RND.nextLong() & ~0x3ffL) | dpd;
            assertEquals("mod10(" + dpd + ")", exp, Rem.mod10(dpd));
            assertEquals("mod10(" + ldpd + ")", exp, Rem.mod10(ldpd));
        }
    }

    @Test
    public void remainderOfPow10() {
        for (int i = 0; i < 1000; i++) {
            final long val = (RND.nextLong() & ~(1L<<63)) % 10000000000000000L;
            final long dpd = dpd(val);
            for (int n = 1; n < 16; n++) {
                final long mod = val % POW10[n];
                assertEquals("Rem.remainderOfPow10(" + dpd + ", " + n + ") = Remainder.ofPow10(" + val + ", " + n + ")",
                        Remainder.ofPow10(mod, n), Rem.remainderOfPow10(dpd, n));
                for (int msd = 0; msd <= 9; msd++) {
                    final long msdPlusMod = msd * POW10[n] + mod;
                    assertEquals("Rem.remainderOfPow10(" + msd + ", " + dpd + ", " + n + ") = Remainder.ofPow10(" + msdPlusMod + ", " + (n+1) + ")",
                            Remainder.ofPow10(msdPlusMod, n+1), Rem.remainderOfPow10(msd, dpd, n));
                }
            }
        }
    }

    private static final long dpd(final long val) {
        return ((long)Declet.intToDpd((int)((val / 1000000000000L) % 1000))) << 40 |
                ((long)Declet.intToDpd((int)((val / 1000000000) % 1000)) << 30) |
                Declet.intToDpd((int)((val / 1000000) % 1000)) << 20 |
                Declet.intToDpd((int)((val / 1000) % 1000)) << 10 |
                Declet.intToDpd((int)(val % 1000));
    }

    @Test(expected = RuntimeException.class)
    public void newInstance() throws Throwable {
        Instance.notAllowed(Rem.class);
    }
}