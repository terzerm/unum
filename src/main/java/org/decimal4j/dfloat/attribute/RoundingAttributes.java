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

public final class RoundingAttributes implements Attributes {

    private final RoundingDirection binaryRoundingDirection;
    private final RoundingDirection decimalRoundingDirection;
    private final Attributes nonRoundingAttributes;

    public RoundingAttributes(final RoundingDirection roundingDirection) {
        this(roundingDirection, roundingDirection, Attributes.DEFAULT);
    }
    public RoundingAttributes(final RoundingDirection binaryRoundingDirection,
                              final RoundingDirection decimalRoundingDirection) {
        this(binaryRoundingDirection, decimalRoundingDirection, Attributes.DEFAULT);
    }
    public RoundingAttributes(final RoundingDirection binaryRoundingDirection,
                              final RoundingDirection decimalRoundingDirection,
                              final Attributes nonRoundingAttributes) {
        this.binaryRoundingDirection = Objects.requireNonNull(binaryRoundingDirection, "binaryRoundingDirection is null");
        this.decimalRoundingDirection = Objects.requireNonNull(decimalRoundingDirection, "decimalRoundingDirection is null");
        this.nonRoundingAttributes = Objects.requireNonNull(nonRoundingAttributes, "nonRoundingAttributes is null");
    }

    @Override
    public final RoundingDirection getBinaryRoundingDirection() {
        return binaryRoundingDirection;
    }

    @Override
    public final RoundingDirection getDecimalRoundingDirection() {
        return decimalRoundingDirection;
    }

    @Override
    public final SignalMode getSignalMode() {
        return nonRoundingAttributes.getSignalMode();
    }

    public final FlagMode getFlagMode(final Flag flag) {
        return nonRoundingAttributes.getFlagMode(flag);
    }

    public final ExceptionHandler getExceptionHandler() {
        return nonRoundingAttributes.getExceptionHandler();
    }
}
