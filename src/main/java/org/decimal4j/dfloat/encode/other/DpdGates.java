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
package org.decimal4j.dfloat.encode.other;

import org.decimal4j.dfloat.encode.Encoding;

/**
 * Densely packed digital encoding.
 */
public class DpdGates {

	private DpdGates() {
		throw new RuntimeException("no DpdGates for you!");
	}
	
	public static final Encoding ENCODING = new Encoding() {
		@Override
		public long encode(long value) {
			return intToDpd((int)value);
		}
	};

	/**
	 * Converts 3 BCD digits to DPD.
	 * 
	 * @param bcd
	 *            12 bit bcd encoding of 3 decimal digits
	 * @return 10 bit dpd encoding of the same 3 decimal digits
	 */
	public static int bcdToDpd(final int bcd) {
		return intDigitsToDpd((bcd & 0x00000f00) >>> 8, (bcd & 0x000000f0) >>> 4, bcd & 0x0000000f);
	}

	/**
	 * Converts an integer value from 0-999 to DPD.
	 * 
	 * @param value
	 *            integer value from 0 to 999
	 * @return 10 bit dpd encoding of the same value
	 */
	public static int intToDpd(final int value) {
		return intDigitsToDpd((value / 100) % 10, (value / 10) % 10, value % 10);
	}

	/**
	 * Converts 3 char digits digits to DPD.
	 * 
	 * @param d2
	 *            most significant digit, '0'-'9'
	 * @param d1
	 *            middle significant digit, '0'-'9'
	 * @param d0
	 *            least significant digit, '0'-'9'
	 * @return 10 bit dpd encoding of the same 3 decimal digits
	 */
	public static int charDigitsToDpd(final char d2, final char d1, final char d0) {
		return intDigitsToDpd(d2 - '0', d1 - '0', d0 - '0');
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
	public static int intDigitsToDpd(final int d2, final int d1, final int d0) {
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
