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
import org.decimal4j.dfloat.ops.Remainder;

import java.io.IOException;

/**
 * Rem offers modulo and remainder operations for DPD encoded values.
 */
public final class Rem {

	private Rem() {
		throw new RuntimeException("No Rem for you!");
	}

	public static final int mod10(final long dpd) {
		final int declet = Declet.dpdToInt((int)(dpd & 0x3ff));
		return Digit.decletToCharDigit(declet, 2) - '0';
	}

	//PRECONDITION: n>0
	public static final Remainder remainderOfPow10(final long dpd, final int n) {
		final int msd = Digit.dpdToCharDigit(dpd, 15 - n) - '0';
		return remainderOfPow10(msd, 15 - n + 1, dpd);
	}

	//PRECONDITION: n>0
	public static final Remainder remainderOfPow10(final int msd, final long dpd, final int n) {
		return remainderOfPow10(msd, 15 - n, dpd);
	}

	private static final Remainder remainderOfPow10(final int msd, final int offset, final long dpd) {
		if (msd > 5) return Remainder.GREATER_THAN_HALF;
		if (msd > 0 & msd < 5) return Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
		final Remainder atLeast, atMost;
		if (msd == 0) {
			atLeast = Remainder.ZERO;
			atMost = Remainder.GREATER_THAN_ZERO_BUT_LESS_THAN_HALF;
		} else {
			//digit == 5
			atLeast = Remainder.EQUAL_TO_HALF;
			atMost = Remainder.GREATER_THAN_HALF;
		}
		int deci = 12;
		for (int shift = 0; deci >= offset; deci-=3, shift+=10) {
			final long declet = (dpd >>> shift) & 0x3ff;
			if (declet != 0) return atMost;
		}
		for (int i = deci + 3; i >= offset; i--) {
			if (Digit.dpdToCharDigit(dpd, i) != '0') {
				return atMost;
			}
		}
		return atLeast;
	}
}
