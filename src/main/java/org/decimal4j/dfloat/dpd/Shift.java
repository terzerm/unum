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

/**
 * Shift deals with left and right shifting of DPD encoded values.
 */
public class Shift {

	private Shift() {
		throw new RuntimeException("No Shift for you!");
	}

	private static final short[] DPD_TO_INT_RSH_1 = initRsh1();
	private static final short[] DPD_TO_INT_RSH_2 = initRsh2();
	private static final short[] DPD_TO_INT_LSH_1 = initLsh1();
	private static final short[] DPD_TO_INT_LSH_2 = initLsh2();

	public static final int shiftRightDeclet(final int dpdHi, final int dpdLo) {
		return Declet.intToDpd(DPD_TO_INT_LSH_2[dpdHi] + DPD_TO_INT_RSH_1[dpdLo]);
	}
	public static final int shiftLeftDeclet(final int dpdHi, final int dpdLo) {
		return Declet.intToDpd(DPD_TO_INT_LSH_1[dpdHi] + DPD_TO_INT_RSH_2[dpdLo]);
	}
	public static final long shiftRight(final long dpd) {
		return shiftRight(0, dpd);
	}
	public static final long shiftRight(final int msd, final long dpd) {
		return shiftRight(
				msd,
				(int)((dpd >>> 40) & 0x3ff),
				(int)((dpd >>> 30) & 0x3ff),
				(int)((dpd >>> 20) & 0x3ff),
				(int)((dpd >>> 10) & 0x3ff),
				(int)(dpd & 0x3ff));
	}
	public static final long shiftRight(final int msd, final int dpd50, final int dpd40, final int dpd30, final int dpd20, final int dpd10) {
		return shiftRightDeclet(dpd20, dpd10) |
				shiftRightDeclet(dpd30, dpd20) << 10 |
				shiftRightDeclet(dpd40, dpd30) << 20 |
				((long)shiftRightDeclet(dpd50, dpd40)) << 30 |
				((long)shiftRightDeclet(msd, dpd50)) << 40;
	}

	public static final long shiftRight2(final int msd, final long dpd) {
		return shiftRight2(
				msd,
				(int)((dpd >>> 40) & 0x3ff),
				(int)((dpd >>> 30) & 0x3ff),
				(int)((dpd >>> 20) & 0x3ff),
				(int)((dpd >>> 10) & 0x3ff),
				(int)(dpd & 0x3ff));
	}
	public static final long shiftRight2(final int msd, final int dpd50, final int dpd40, final int dpd30, final int dpd20, final int dpd10) {
		return shiftLeftDeclet(dpd20, dpd10) |
				shiftLeftDeclet(dpd30, dpd20) << 10 |
				shiftLeftDeclet(dpd40, dpd30) << 20 |
				((long)shiftLeftDeclet(dpd50, dpd40)) << 30 |
				((long)shiftLeftDeclet(msd, dpd50)) << 40;
	}

	private static final int msd(final int dpd60) {
		return Rem.mod10(dpd60);
	}
	private static final long canonicalize(final int dpd60,
										   final int dpd50,
										   final int dpd40,
										   final int dpd30,
										   final int dpd20,
										   final int dpd10) {
		return ((long)msd(dpd60)) << 50 | ((long)Declet.canonicalize(dpd50)) << 40 | ((long)Declet.canonicalize(dpd40)) << 30 |
				Declet.canonicalize(dpd30) << 20 | Declet.canonicalize(dpd20) << 10 | Declet.canonicalize(dpd10);
	}
	private static final long shiftRight0to2(final int dpd60,
											 final int dpd50,
											 final int dpd40,
											 final int dpd30,
											 final int dpd20,
											 final int dpd10,
											 final int n) {
		if (n == 1) {
			return shiftRight(dpd60, dpd50, dpd40, dpd30, dpd20, dpd10);
		}
		if (n == 2) {
			return shiftRight2(dpd60, dpd50, dpd40, dpd30, dpd20, dpd10);
		}
		return canonicalize(dpd60, dpd50, dpd40, dpd30, dpd20, dpd10);
	}

	public static final long shiftRight(final long dpd, final int n) {
		return shiftRight(0, dpd, n);
	}
	public static final long shiftRight(final int msd, final long dpd, final int n) {
		final int dpd10 = (int)(dpd & 0x3ff);
		final int dpd20 = (int)((dpd >>> 10) & 0x3ff);
		final int dpd30 = (int)((dpd >>> 20) & 0x3ff);
		final int dpd40 = (int)((dpd >>> 30) & 0x3ff);
		final int dpd50 = (int)((dpd >>> 40) & 0x3ff);
		//binary search, optimized for small n
		if (n < 6) {
			if (n < 3) {
				return shiftRight0to2(msd, dpd50, dpd40, dpd30, dpd20, dpd10, n);
			}
			return shiftRight0to2(0, msd, dpd50, dpd40, dpd30, dpd20, n-3);
		}
		if (n < 12) {
			if (n < 9) {
				return shiftRight0to2(0, 0, msd, dpd50, dpd40, dpd30, n - 6);
			}
			return shiftRight0to2(0, 0, 0, msd, dpd50, dpd40, n-9);
		}
		if (n < 15) {
			return shiftRight0to2(0, 0, 0, 0, msd, dpd50, n-12);
		}
		return n < 16 ? msd : 0;
	}

	public static final long shiftLeft(final long dpd) {
		return shiftLeft(
				(int)((dpd >>> 40) & 0x3ff),
				(int)((dpd >>> 30) & 0x3ff),
				(int)((dpd >>> 20) & 0x3ff),
				(int)((dpd >>> 10) & 0x3ff),
				(int)(dpd & 0x3ff));
	}
	public static final long shiftLeft2(final long dpd) {
		return shiftLeft2(
				(int)((dpd >>> 40) & 0x3ff),
				(int)((dpd >>> 30) & 0x3ff),
				(int)((dpd >>> 20) & 0x3ff),
				(int)((dpd >>> 10) & 0x3ff),
				(int)(dpd & 0x3ff));
	}
	public static final long shiftLeft(final int dpd50, final int dpd40, final int dpd30, final int dpd20, final int dpd10) {
		final long msd = DPD_TO_INT_RSH_2[dpd50];
		return (msd << 50) | shiftRight2(dpd50, dpd40, dpd30, dpd20, dpd10, 0);
	}
	public static final long shiftLeft2(final int dpd50, final int dpd40, final int dpd30, final int dpd20, final int dpd10) {
		final long msd = Digit.decletToIntDigit(dpd50, 1);
		return (msd << 50) | shiftRight(dpd50, dpd40, dpd30, dpd20, dpd10, 0);
	}
	private static final long shiftLeft0to2(final int dpd60,
											final int dpd50,
											final int dpd40,
											final int dpd30,
											final int dpd20,
											final int dpd10,
											final int n) {
		if (n == 1) {
			return shiftLeft(dpd50, dpd40, dpd30, dpd20, dpd10);
		}
		if (n == 2) {
			return shiftLeft2(dpd50, dpd40, dpd30, dpd20, dpd10);
		}
		return canonicalize(dpd60, dpd50, dpd40, dpd30, dpd20, dpd10);
	}

	public static final long shiftLeft(final long dpd, final int n) {
		final int dpd10 = (int)(dpd & 0x3ff);
		final int dpd20 = (int)((dpd >>> 10) & 0x3ff);
		final int dpd30 = (int)((dpd >>> 20) & 0x3ff);
		final int dpd40 = (int)((dpd >>> 30) & 0x3ff);
		final int dpd50 = (int)((dpd >>> 40) & 0x3ff);
		//binary search, optimized for small n
		if (n < 6) {
			if (n < 3) {
				return shiftLeft0to2(0, dpd50, dpd40, dpd30, dpd20, dpd10, n);
			}
			return shiftLeft0to2(dpd50, dpd40, dpd30, dpd20, dpd10, 0, n-3);
		}
		if (n < 12) {
			if (n < 9) {
				return shiftLeft0to2(dpd40, dpd30, dpd20, dpd10, 0, 0, n-6);
			}
			return shiftLeft0to2(dpd30, dpd20, dpd10, 0, 0, 0, n-9);
		}
		if (n < 15) {
			return shiftLeft0to2(dpd20, dpd10, 0, 0, 0, 0, n-12);
		}
		return shiftLeft0to2(dpd10, 0, 0, 0, 0, 0, n-15);
	}

	private static final short[] initRsh1() {
		final short[] rsh = new short[1024];
		for (int i = 0; i < 1024; i++) {
			rsh[i] = (short)(10*Digit.decletToIntDigit(i, 0) + Digit.decletToIntDigit(i, 1));
		}
		return rsh;
	}
	private static final short[] initRsh2() {
		final short[] rsh = new short[1024];
		for (int i = 0; i < 1024; i++) {
			rsh[i] = (short)Digit.decletToIntDigit(i, 0);
		}
		return rsh;
	}
	private static final short[] initLsh1() {
		final short[] lsh = new short[1024];
		for (int i = 0; i < 1024; i++) {
			lsh[i] = (short)(100*Digit.decletToIntDigit(i, 1) + 10*Digit.decletToIntDigit(i, 2));
		}
		return lsh;
	}
	private static final short[] initLsh2() {
		final short[] lsh = new short[1024];
		for (int i = 0; i < 1024; i++) {
			lsh[i] = (short)(100*Digit.decletToIntDigit(i, 2));
		}
		return lsh;
	}
}
