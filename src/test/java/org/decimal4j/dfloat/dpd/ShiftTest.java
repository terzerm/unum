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

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ShiftTest {

    private static final Random RND = new Random();

    @Test
    public void shiftDeclet() {
        for (int dpdHi = 0; dpdHi < 1024; dpdHi++) {
            for (int dpdLo = 0; dpdLo < 1024; dpdLo++) {
                final int hi = Declet.dpdToInt(dpdHi);
                final int lo = Declet.dpdToInt(dpdLo);
                //final int d0 = (hi / 100) % 10;
                final int d1 = (hi / 10) % 10;
                final int d2 = hi % 10;
                final int d3 = (lo / 100) % 10;
                final int d4 = (lo / 10) % 10;
                //final int d5 = lo % 10;
                final int lsh = Declet.intToDpd(d1*100 + d2*10 + d3);
                final int rsh = Declet.intToDpd(d2*100 + d3*10 + d4);
                assertEquals("shiftLeftDeclet(" + dpdHi + "," + dpdLo + ")", lsh, Shift.shiftLeftDeclet(dpdHi, dpdLo));
                assertEquals("shiftRightDeclet(" + dpdHi + "," + dpdLo + ")", rsh, Shift.shiftRightDeclet(dpdHi, dpdLo));
            }
        }
    }

    @Test
    public void shift() {
        final int[] declets = new int[5];
        final int[] intlets = new int[5];
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 5; j++) {
                declets[j] = RND.nextInt(1024);
                intlets[j] = Declet.dpdToInt(declets[j]);
            }
            final int msd = RND.nextInt(10);
            final long dpd = (((long)declets[0])<<40) | (((long)declets[1])<<30) | (declets[2]<<20) | (declets[3]<<10) | declets[4];
            final long num = ((long)msd) << 50 | Dpd.canonicalize(dpd);
            final int[] digits = digits(intlets);
            for (int j = 0; j < 16; j++) {
                final long lsh = lsh(j, digits);
                final long rsh = rsh(j, msd, digits);
                try {
                    assertEquals("shiftLeft(" + dpd + ", " + j + ")", lsh, Shift.shiftLeft(dpd, j));
                    assertEquals("shiftRight(" + msd + ", " + dpd + ", " + j + ")", rsh, Shift.shiftRight(msd, dpd, j));
                } catch (AssertionError e) {
                    System.out.println("num=" + msd + "'" + Digit.dpdToString(dpd));
                    System.out.println("lsh=" + (lsh >>> 50) + "'" + Digit.dpdToString(lsh));
                    System.out.println("rsh=" + (rsh >>> 50) + "'" + Digit.dpdToString(rsh));
                    System.out.println("aLS=" + (Shift.shiftLeft(dpd, j) >>> 50) + "'" + Digit.dpdToString(Shift.shiftLeft(dpd, j)));
                    System.out.println("aRS=" + (Shift.shiftRight(msd, dpd, j) >>> 50) + "'" + Digit.dpdToString(Shift.shiftRight(msd, dpd, j)));
                    System.out.println("can=" + Dpd.isCanonical(rsh));
                    System.out.println("cRS=" + Dpd.isCanonical(Shift.shiftRight(msd, dpd, j)));
                    throw e;
                }
            }
            assertEquals("shiftLeft(" + dpd + ", " + 0 + ")", Dpd.canonicalize(dpd), Shift.shiftLeft(dpd, 0));
            assertEquals("shiftRight(" + msd + ", " + dpd + ", " + 0 + ")", num, Shift.shiftRight(msd, dpd, 0));
            assertEquals("shiftLeft(" + dpd + ", " + 16 + ")", 0, Shift.shiftLeft(dpd, 16));
            assertEquals("shiftRight(" + msd + ", " + dpd + ", " + 16 + ")", 0, Shift.shiftRight(msd, dpd, 16));
        }
    }

    private static final int[] digits(final int... declets) {
        final int[] digits = new int[15];
        for (int i = 0; i < 5; i++) {
            digits[i*3] = (declets[i] / 100) % 10;
            digits[i*3+1] = (declets[i] / 10) % 10;
            digits[i*3+2] = declets[i] % 10;
        }
        return digits;
    }

    private static final long lsh(final int n,
                                  final int ... digits) {
        if (n == 0) return dpd(0, digits);
        if (n > 15) return 0;
        final int[] shifted = new int[15];
        System.arraycopy(digits, n, shifted, 0, 15 - n);
        return dpd(digits[n-1], shifted);
    }
    private static final long rsh(final int n,
                                  final int msd,
                                  final int ... digits) {
        if (n == 0) return dpd(msd, digits);
        if (n > 15) return 0;
        final int[] shifted = new int[15];
        System.arraycopy(digits, 0, shifted, n, 15 - n);
        shifted[n-1] = msd;
        return dpd(0, shifted);
    }
    private static final long dpd(final int msd,
                                  final int[] digits) {
        return ((long)msd)<<50
                | ((long)Digit.intDigitsToDpd(digits[0], digits[1], digits[2]))<<40
                | ((long)Digit.intDigitsToDpd(digits[3], digits[4], digits[5]))<<30
                | Digit.intDigitsToDpd(digits[6], digits[7], digits[8])<<20
                | Digit.intDigitsToDpd(digits[9], digits[10], digits[11])<<10
                | Digit.intDigitsToDpd(digits[12], digits[13], digits[14]);
    }


    @Test(expected = RuntimeException.class)
    public void newInstance() throws Throwable {
        Instance.notAllowed(Shift.class);
    }
}