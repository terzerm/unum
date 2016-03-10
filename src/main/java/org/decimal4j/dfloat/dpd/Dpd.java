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

/**
 * Densely packed digital encoding.
 */
public class Dpd {

	private Dpd() {
		throw new RuntimeException("No Dpd for you!");
	}

	public static long canonicalize(final long dpd) {
		return ((long)Declet.canonicalize((int) (dpd & 0x3ff))) |
				(((long)Declet.canonicalize((int) ((dpd >>> 10) & 0x3ff)) << 10)) |
				(((long)Declet.canonicalize((int) ((dpd >>> 20) & 0x3ff)) << 20)) |
				(((long)Declet.canonicalize((int) ((dpd >>> 30) & 0x3ff)) << 30)) |
				(((long)Declet.canonicalize((int) ((dpd >>> 40) & 0x3ff)) << 40));
	}

	public static boolean isCanonical(final long dpd) {
		return Declet.isCanonical((int) (dpd & 0x3ff)) &
				Declet.isCanonical((int) ((dpd >>> 10) & 0x3ff)) &
				Declet.isCanonical((int) ((dpd >>> 20) & 0x3ff)) &
				Declet.isCanonical((int) ((dpd >>> 30) & 0x3ff)) &
				Declet.isCanonical((int) ((dpd >>> 40) & 0x3ff));
	}

	public static long add(final long dpdA, final long dpdB) {
		final int sum10 = Declet.add((int)(dpdA & 0x3ff), (int)(dpdB & 0x3ff), 0);
		final int sum20 = Declet.add((int)((dpdA >>> 10) & 0x3ff), (int)((dpdB >> 10) & 0x3ff), sum10 >>> 10);
		final int sum30 = Declet.add((int)((dpdA >>> 20) & 0x3ff), (int)((dpdB >> 20) & 0x3ff), sum20 >>> 10);
		final int sum40 = Declet.add((int)((dpdA >>> 30) & 0x3ff), (int)((dpdB >> 30) & 0x3ff), sum30 >>> 10);
		final int sum50 = Declet.add((int)((dpdA >>> 40) & 0x3ff), (int)((dpdB >> 40) & 0x3ff), sum40 >>> 10);
		return (sum10 & 0x3ffL) |
				((sum20 & 0x3ffL) << 10) |
				((sum30 & 0x3ffL) << 20) |
				((sum40 & 0x3ffL) << 30) |
				((sum50 & 0x3ffL) << 40) |
				((sum50 & 0x400L) << 50);
	}

	public static long inc(final long dpd) {
		return inc(dpd, 1);
	}

	//PREDONDITION: inc <= 1000
	public static long inc(final long dpd, final int inc) {
		final long sum10 = Declet.inc((int)(dpd & 0x3ff), inc);
		if ((sum10 >>> 10) == 0) {
			return (dpd & 0x0003fffffffffc00L) | (sum10 & 0x3ff);
		}
		final long sum20 = Declet.inc((int)((dpd >>> 10) & 0x3ff), 1);
		if ((sum20 >>> 10) == 0) {
			return (dpd & 0x0003fffffff00000L) | ((sum20 & 0x3ff) << 10) | (sum10 & 0x3ff);
		}
		final long sum30 = Declet.inc((int)((dpd >>> 20) & 0x3ff), 1);
		if ((sum30 >>> 10) == 0) {
			return (dpd & 0x0003ffffc0000000L) | ((sum30 & 0x3ff) << 20) | ((sum20 & 0x3ff) << 10) | (sum10 & 0x3ff);
		}
		final long sum40 = Declet.inc((int)((dpd >>> 30) & 0x3ff), 1);
		if ((sum40 >>> 10) == 0) {
			return (dpd & 0x0003ff0000000000L) | ((sum40 & 0x3ff) << 30) | ((sum30 & 0x3ff) << 20) | ((sum20 & 0x3ff) << 10) | (sum10 & 0x3ff);
		}
		final long sum50 = Declet.inc((int)((dpd >>> 40) & 0x3ff), 1);
		return (sum10 & 0x3ffL) |
				((sum20 & 0x3ffL) << 10) |
				((sum30 & 0x3ffL) << 20) |
				((sum40 & 0x3ffL) << 30) |
				((sum50 & 0x7ffL) << 40);
	}

	public static long dec(final long dpd) {
		return dec(dpd, 1);
	}

	//PREDONDITION: dec <= 1000
	public static long dec(final long dpd, final int dec) {
		final long dec10 = Declet.dec((int)(dpd & 0x3ff), dec);
		if ((dec10 >>> 10) == 0) {
			return (dpd & 0x0003fffffffffc00L) | (dec10 & 0x3ff);
		}
		final long dec20 = Declet.dec((int)((dpd >>> 10) & 0x3ff), 1);
		if ((dec20 >>> 10) == 0) {
			return (dpd & 0x0003fffffff00000L) | ((dec20 & 0x3ff) << 10) | (dec10 & 0x3ff);
		}
		final long dec30 = Declet.dec((int)((dpd >>> 20) & 0x3ff), 1);
		if ((dec30 >>> 10) == 0) {
			return (dpd & 0x0003ffffc0000000L) | ((dec30 & 0x3ff) << 20) | ((dec20 & 0x3ff) << 10) | (dec10 & 0x3ff);
		}
		final long dec40 = Declet.dec((int)((dpd >>> 30) & 0x3ff), 1);
		if ((dec40 >>> 10) == 0) {
			return (dpd & 0x0003ff0000000000L) | ((dec40 & 0x3ff) << 30) | ((dec30 & 0x3ff) << 20) | ((dec20 & 0x3ff) << 10) | (dec10 & 0x3ff);
		}
		final long dec50 = Declet.dec((int)((dpd >>> 40) & 0x3ff), 1);
		return (dec10 & 0x3ffL) |
				((dec20 & 0x3ffL) << 10) |
				((dec30 & 0x3ffL) << 20) |
				((dec40 & 0x3ffL) << 30) |
				((dec50 & 0x7ffL) << 40);
	}

	public static long sub(final long dpdA, final long dpdB) {
		final int sub10 = Declet.sub((int)(dpdA & 0x3ff), (int)(dpdB & 0x3ff), 0);
		final int sub20 = Declet.sub((int)((dpdA >>> 10) & 0x3ff), (int)((dpdB >> 10) & 0x3ff), sub10 >>> 10);
		final int sub30 = Declet.sub((int)((dpdA >>> 20) & 0x3ff), (int)((dpdB >> 20) & 0x3ff), sub20 >>> 10);
		final int sub40 = Declet.sub((int)((dpdA >>> 30) & 0x3ff), (int)((dpdB >> 30) & 0x3ff), sub30 >>> 10);
		final int sub50 = Declet.sub((int)((dpdA >>> 40) & 0x3ff), (int)((dpdB >> 40) & 0x3ff), sub40 >>> 10);
		return (sub10 & 0x3ffL) |
				((sub20 & 0x3ffL) << 10) |
				((sub30 & 0x3ffL) << 20) |
				((sub40 & 0x3ffL) << 30) |
				((sub50 & 0x3ffL) << 40) |
				((sub50 & 0x400L) << 50);
	}

	public static int compare(final long dpdA, final long dpdB) {
		int cmp;
		cmp = Declet.compare((int)((dpdA >>> 40) & 0x3ff), (int)((dpdB >>> 40) & 0x3ff));
		if (cmp != 0) return cmp;
		cmp = Declet.compare((int)((dpdA >>> 30) & 0x3ff), (int)((dpdB >>> 30) & 0x3ff));
		if (cmp != 0) return cmp;
		cmp = Declet.compare((int)((dpdA >>> 20) & 0x3ff), (int)((dpdB >>> 20) & 0x3ff));
		if (cmp != 0) return cmp;
		cmp = Declet.compare((int)((dpdA >>> 10) & 0x3ff), (int)((dpdB >>> 10) & 0x3ff));
		if (cmp != 0) return cmp;
		return Declet.compare((int)(dpdA & 0x3ff), (int)(dpdB & 0x3ff));
	}

	public static boolean isZero(final long dpd) {
		return 0 == (dpd & Decimal64.COEFF_CONT_MASK);
	}

	public static int numberOfLeadingZeros(final long dpd) {
		int nlz = Declet.numberOfLeadingZeros((int)((dpd >>> 40) & 0x3ff));
		if (nlz < 3) return nlz;
		int d = (int)((dpd >>> 20) & 0xfffff);
		if (d == 0) {nlz += 6; d = (int)(dpd & 0xfffff);}
		if ((d >>> 10) == 0) {nlz += 3; d <<= 10;}
		nlz += Declet.numberOfLeadingZeros(d >>> 10);
		return nlz;
	}

	public static int numberOfTrailingZeros(final long dpd) {
		int ntz = Declet.numberOfTrailingZeros((int)(dpd & 0x3ff));
		if (ntz < 3) return ntz;
		int d = (int)((dpd >>> 10) & 0xfffff);
		if (d == 0) {ntz += 6; d = (int)((dpd >>> 30) & 0xfffff);}
		if ((d & 0x3ff) == 0) {ntz += 3; d >>>= 10;}
		ntz += Declet.numberOfTrailingZeros(d & 0x3ff);
		return ntz;
	}

}
