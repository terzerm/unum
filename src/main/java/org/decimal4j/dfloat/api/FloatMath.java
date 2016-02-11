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
package org.decimal4j.dfloat.api;

import org.decimal4j.dfloat.encode.Decimal64;
import org.decimal4j.dfloat.encode.Dpd;
import org.decimal4j.dfloat.ops.Add;
import org.decimal4j.dfloat.ops.Sign;
import org.decimal4j.dfloat.ops.Sub;
import org.decimal4j.dfloat.signal.Signal;

public final class FloatMath {

	public static final long ZERO = Decimal64.ZERO;
	public static final long MIN_NORMAL = 0;//FIXME
	public static final long MAX_NORMAL = 0;//FIXME

	/**
	 * Copy the sign of b into a.
	 *
	 * @param a number to return with sign of b
	 * @param b sign to copy into a
	 * @return a with sign of b
	 */
	public static long copySign(final long a, final long b) {
		return Sign.copySign(a, b);
	}
	public static long abs(final long a) {
		return Sign.clearSign(a);
	}
	public static long negate(final long a) {
		return Sign.flipSign(a);
	}
	public static long add(final long a, final long b) {
		return Add.add(a, b);
	}

	public static long subtract(final long a, final long b) {
		return Sub.subtract(a, b);
	}

	public static boolean isInfinite(final long a) {
		return Decimal64.isInfinite(a);
	}

	public static boolean isFinite(final long a) {
		return Decimal64.isFinite(a);
	}

	public static final boolean isNormal(final long a) {
		return Decimal64.isNormal(a);
	}

	public static final boolean isSubnormal(final long a) {
		return Decimal64.isSubnormal(a);
	}

	public static final boolean isCanonical(final long a) {
		return Decimal64.isCanonical(a);
	}

	public static boolean isZero(final long a) {
		return Decimal64.isZero(a);
	}

	public static boolean isPositive(final long a) {
		return !isNaN(a) & !isSignMinus(a) & !isZero(a);
	}

	public static boolean isNegative(final long a) {
		return !isNaN(a) & isSignMinus(a) & !isZero(a);
	}

	public static double signum(final long a) {
		return Sign.signum(a);
	}

	public static boolean isSignMinus(final long a) {
		return Sign.isSignMinus(a);
	}

	public static boolean isNaN(final long a) {
		return Decimal64.isNaN(a);
	}

	public static boolean isSignalingNaN(final long a) {
		return Decimal64.isSignalingNaN(a);
	}

	public static boolean isQuietNaN(final long a) {
		return Decimal64.isQuietNaN(a);
	}

	public static ValueClass classFor(final long a) {
		return ValueClass.classFor(a);
	}

	private FloatMath() {
		throw new RuntimeException("No FloatMath for you!");
	}
}
