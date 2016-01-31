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
package org.decimal4j.dfloat.attribute;

import java.util.Objects;

/**
 * Exception thrown by {@link ExceptionHandlers#ThrowException}.
 */
public class RaisedFlagException extends ArithmeticException {

    private final String operation;
    private final long firstOperand;
    private final long secondOperand;
    private final long result;
    private final Flag flag;
    private final Flag otherFlag;
    private final Attributes attributes;

    public RaisedFlagException(final String operation,
                               final long firstOperand, final long secondOperand,
                               final long result,
                               final Flag flag, final Flag otherFlag,
                               final Attributes attributes) {
        super(otherFlag == null ? flag.name() : flag.name() + " + " + otherFlag.name());
        this.operation = Objects.requireNonNull(operation, "operation is null");
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.result = result;
        this.flag = Objects.requireNonNull(flag, "flag is null");
        this.otherFlag = otherFlag;//null allowed
        this.attributes = Objects.requireNonNull(attributes, "attributes is null");
    }

    public final String getOperation() {
        return operation;
    }

    public final long getFirstOperand() {
        return firstOperand;
    }

    public final long getSecondOperand() {
        return secondOperand;
    }

    public final long getResult() {
        return result;
    }

    public final Flag getFlag() {
        return flag;
    }

    public final Flag getOtherFlag() {
        return otherFlag;
    }

    public final Attributes getAttributes() {
        return attributes;
    }
}
