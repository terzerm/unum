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
package org.decimal4j.dfloat.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class AbstractFloatArithmetic implements FloatArithmetic {

	@Override
	public RoundingMode getRoundingMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int signum(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compare(long dFloat1, long dFloat2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long add(long dFloat1, long dFloat2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addLong(long dFloat, long lValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long subtract(long dFloatMinuend, long dFloatSubtrahend) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long subtractLong(long dFloat, long lValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long multiply(long dFloat1, long dFloat2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long multiplyByLong(long dFloat, long lValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long multiplyByPowerOf10(long dFloat, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divide(long dFloatDividend, long dFloatDivisor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divideByLong(long dFloatDividend, long lDivisor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divideByPowerOf10(long dFloat, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long abs(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long negate(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long invert(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long sqrt(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long pow(long dFloatBase, int exponent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long round(long dFloat, int precision) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromLong(long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromFloat(float value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromDouble(double value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long parse(String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long parse(CharSequence value, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long toLong(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float toFloat(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double toDouble(long dFloat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal toBigDecimal(long dFloat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal toBigDecimal(long dFloat, int scale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(long dFloat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toString(long dFloat, Appendable appendable) throws IOException {
		// TODO Auto-generated method stub

	}

}
