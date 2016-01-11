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

public class DpdFunctions {
	public static final long min(final long from, final long to, final Encoding encoding) {
		long min = Long.MAX_VALUE;
		for (long i = from; i < to; i++) {
			min = Math.min(min, encoding.encode(i));
		}
		return min;
	}
	public static final long max(final long from, final long to, final Encoding encoding) {
		long max = Long.MIN_VALUE;
		for (long i = from; i < to; i++) {
			max = Math.max(max, encoding.encode(i));
		}
		return max;
	}
	public static final Encoding invert(final long from, final long to, final Encoding encoding) {
		final long min = min(from, to, encoding);
		final long max = 1024;//max(from, to, encoding);
		final long[] mapping = new long[(int)(max - min + 1)];
		for (long i = from; i < to; i++) {
			mapping[(int)(encoding.encode(i) - min)] = i;
			if (i == 888 | i == 889 | i == 898 | i == 899 | i == 988 | i == 989 | i == 998 | i == 999) {
				mapping[(int)((encoding.encode(i) | 0x100) - min)] = i;
				mapping[(int)((encoding.encode(i) | 0x200) - min)] = i;
				mapping[(int)((encoding.encode(i) | 0x300) - min)] = i;
			}
		}
		return new Encoding() {
			@Override
			public long encode(long value) {
				final int index = (int)(value - min);
				return index < mapping.length ? mapping[index] : 0;
			}
		};
	}

}
