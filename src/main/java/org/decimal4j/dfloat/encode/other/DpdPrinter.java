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
public class DpdPrinter {
	
	public static void main(String[] args) {
//		printArray(0, 1000, DpdGates.ENCODING);
//		printArray(0, 1000, Dpd.ENCODING);
//		printArray(0, 1024, invert(0, 1000, DpdGates.ENCODING));
//		printArray(0, 1024, DpdFunctions.invert(0, 1000, Dpd.ENCODING));
//		printWikiSample(DpdGates.ENCODING);
//		printWikiSample(Dpd.ENCODING);
	}
	
	
	public static void printArray(final long from, final long to, final Encoding encoding) {
		final long factor = 1;
		//final long factor = 1000000000000L;
		for (long i = from; i < to; i++) {
			System.out.print(i == from ? '{' : ',');
			System.out.print(encoding.encode(i));
//			System.out.print("\"" + ((encoding.encode(i)/100)%10) + ((encoding.encode(i)/10)%10) + ((encoding.encode(i)/1)%10) + "\"");
		}
		System.out.println('}');
	}
	
	public static void printWikiSample(final Encoding encoding) {
		printBinary(0, encoding);
		printBinary(5, encoding);
		printBinary(9, encoding);
		printBinary(55, encoding);
		printBinary(79, encoding);
		printBinary(80, encoding);
		printBinary(99, encoding);
		printBinary(555, encoding);
		printBinary(999, encoding);
	}
	
	public static void printBinary(final int value, final Encoding encoding) {
		final String encoded = Long.toBinaryString(encoding.encode(value));
		System.out.println(value + ":\t" + "00000000000000000000000000000000000000000000000000".substring(encoded.length()) + encoded);
	}
}
