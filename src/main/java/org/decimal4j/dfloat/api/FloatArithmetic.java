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

import org.decimal4j.dfloat.attribute.Attributes;
import org.decimal4j.dfloat.attribute.RoundingDirection;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public interface FloatArithmetic {
	Attributes getAttributes();

	int signum(long dFloat);

	int compare(long dFloat1, long dFloat2);

	long add(long dFloat1, long dFloat2);

	long addLong(long dFloat, long lValue);

	long subtract(long dFloatMinuend, long dFloatSubtrahend);

	long subtractLong(long dFloat, long lValue);

	long multiply(long dFloat1, long dFloat2);

	long multiplyByLong(long dFloat, long lValue);

	long multiplyByPowerOf10(long dFloat, int n);

	long divide(long dFloatDividend, long dFloatDivisor);

	long divideByLong(long dFloatDividend, long lDivisor);

	long divideByPowerOf10(long dFloat, int n);

	long abs(long dFloat);

	long negate(long dFloat);

	long invert(long dFloat);

	long sqrt(long dFloat);

	long pow(long dFloatBase, int exponent);

	long round(long dFloat, int precision);

	long fromLong(long value);

	long fromFloat(float value);

	long fromDouble(double value);

	long fromBigInteger(BigInteger value);

	long fromBigDecimal(BigDecimal value);

	long fromUnscaled(long unscaledValue, int scale);

	long parse(String value);

	long parse(CharSequence value, int start, int end);

	long toLong(long dFloat);

	float toFloat(long dFloat);

	double toDouble(long dFloat);

	BigDecimal toBigDecimal(long dFloat);

	BigDecimal toBigDecimal(long dFloat, int scale);

	String toString(long dFloat);

	void toString(long dFloat, Appendable appendable) throws IOException;
}
