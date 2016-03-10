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

public class RandomDpd {

    private static Random RND = new Random();

    public static final long randomDpd() {
        return RND.nextLong() & Decimal64.COEFF_CONT_MASK;
    }

    public static final long randomCanonicalDpd() {
        return Dpd.canonicalize(randomDpd());
    }

    public static final long randomDpd(int declet, int index) {
        return (randomDpd() & ~(0x3ffL << (index*10))) | (((long)declet) << (index*10));
    }

    public static final long randomCanonicalDpd(int declet, int index) {
        return Dpd.canonicalize(randomDpd(declet, index));
    }

}