/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.dfloat.encode.other;

/**
 * Binary coded decimal using 4 bits to encode a single decimal digit.
 */
public class Bcd {

	private Bcd() {
		throw new RuntimeException("no Bcd for you!");
	}
	
	/**
	 * Converts 3 BCD digits to DPD.
	 * 
	 * @param bcd
	 *            12 bit bcd encoding of 3 decimal digits
	 * @return 10 bit dpd encoding of the same 3 decimal digits
	 */
	public static int intDigitToBcd(final int bcd) {
		return ((((bcd ^ 0x4) >> 1) | ((bcd ^ 0x2) >> 1)) & (bcd & 0x8)) | (((bcd ^ 0x8) >> 1) & (bcd & 0x4)) | (((bcd ^ 0x8) >> 2) & (bcd & 0x2)) | (bcd & 0x1);
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
	public static int intDigitsToBcd(final int d2, final int d1, final int d0) {
		final int raw = (d2 << 8) | (d1 << 4) | d0;
		final int small = raw & 0x777;
		final int large = raw & 0x999;
		return small | (large & (~small));
	}
}
