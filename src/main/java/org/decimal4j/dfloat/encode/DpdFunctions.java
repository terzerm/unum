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
package org.decimal4j.dfloat.encode;

public class DpdFunctions {
	public static final int min(final int from, final int to, final Encoding encoding) {
		int min = Integer.MAX_VALUE;
		for (int i = from; i < to; i++) {
			min = Math.min(min, encoding.encode(i));
		}
		return min;
	}
	public static final int max(final int from, final int to, final Encoding encoding) {
		int max = Integer.MIN_VALUE;
		for (int i = from; i < to; i++) {
			max = Math.max(max, encoding.encode(i));
		}
		return max;
	}
	public static final Encoding invert(final int from, final int to, final Encoding encoding) {
		final int min = min(from, to, encoding);
		final int max = max(from, to, encoding);
		final int[] mapping = new int[max - min + 1];
		for (int i = from; i < to; i++) {
			mapping[encoding.encode(i) - min] = i;
		}
		return new Encoding() {
			@Override
			public int encode(int value) {
				return mapping[value - min];
			}
		};
	}

}
