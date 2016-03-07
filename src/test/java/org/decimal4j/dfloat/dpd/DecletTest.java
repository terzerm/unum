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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class DecletTest {

    private static final List<Integer> NON_CANONICALS = Arrays.asList(366, 367, 382, 383, 494, 495, 510, 511, 622, 623, 638, 639, 750, 751, 766, 767, 878, 879, 894, 895, 1006, 1007, 1022, 1023);
    private static final List<Integer> REP_CANONICALS = Arrays.asList(110, 111, 126, 127, 238, 239, 254, 255, 110, 111, 126, 127, 238, 239, 254, 255, 110, 111, 126, 127, 238, 239, 254, 255);

    @Test
    @Parameters(method = "dpdValues")
    @TestCaseName("{method}({0})")
    public void isCanonical(final int a) {
        final boolean expected = !NON_CANONICALS.contains(a);
        assertEquals("isCanonical(" + a + ")", expected, Declet.isCanonical(a));
    }

    @Test
    @Parameters(method = "dpdValues")
    @TestCaseName("{method}({0})")
    public void canonicalize(final int a) {
        final int i = Collections.binarySearch(NON_CANONICALS, a);
        final int expected = i < 0 ? a : REP_CANONICALS.get(i);
        assertEquals("canonicalize(" + a + ")", expected, Declet.canonicalize(a));
    }

    @Test
    @Parameters(method = "dpdValues")
    @TestCaseName("{method}({0})")
    public void dpdToInt(final int a) {
        final int actual = Declet.dpdToInt(a);

        final int i = Collections.binarySearch(NON_CANONICALS, a);
        final int expected = i < 0 ? a : REP_CANONICALS.get(i);
//        final int expected = intDigitsToDpd(a / 100, (a / 10) % 10, a % 10);
//        assertEquals("dpdToInt(" + a + ")", expected, actual);
        assertEquals("intToDpd(dpdToInt(" + a + "))", expected, Declet.intToDpd(Declet.dpdToInt(a)));
    }

    @Test
    @Parameters(method = "intValues")
    @TestCaseName("{method}({0})")
    public void intToDpd(final int a) {
        final int actual = Declet.intToDpd(a);

        final int expected = intDigitsToDpd(a / 100, (a / 10) % 10, a % 10);
        assertEquals("intToDpd(" + a + ")", expected, actual);
        assertEquals("dpdToInt(intToDpd(" + a + "))", a, Declet.dpdToInt(actual));
    }

    @Test
    @Parameters(method = "dpdIntValues")
    @TestCaseName("{method}({0},{1})")
    public void inc(final int a, final int b) {
        final int actual = Declet.inc(a, b);

        final int valA = Declet.dpdToInt(a);
        final int sum = valA + b;
        final int expected = sum < 1000 ? Declet.intToDpd(sum) : (1<<10) | Declet.intToDpd(sum - 1000);
        assertEquals("inc(" + a + ", " + b + ")", expected, actual);
    }

    public Object[] dpdIntValues() {
        final List<Object[]> values = new ArrayList<Object[]>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                values.add(new Object[] {i, j});
                values.add(new Object[] {j, i});
            }
        }
        for (int i = 0; i < 1000; i+=99) {
            for (int j = 0; j < 1000; j+=97) {
                values.add(new Object[] {i, j});
                values.add(new Object[] {j, i});
            }
        }
        for (int i = 990; i < 1000; i++) {
            for (int j = 0; j < 10; j++) {
                values.add(new Object[] {i, j});
                values.add(new Object[] {j, i});
            }
        }
        return values.toArray();
    }

    public Object[] intValues() {
        final List<Object[]> values = new ArrayList<Object[]>();
        for (int i = 0; i < 1000; i++) {
            values.add(new Object[] {i});
        }
        return values.toArray();
    }

    public Object[] dpdValues() {
        final List<Object[]> values = new ArrayList<Object[]>();
        for (int i = 0; i < 1024; i++) {
            values.add(new Object[] {i});
        }
        return values.toArray();
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