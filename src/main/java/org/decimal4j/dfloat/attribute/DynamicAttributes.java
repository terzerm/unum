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

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DynamicAttributes implements Attributes, Cloneable {
    private RoundingDirection binaryRoundingDirection;
    private RoundingDirection decimalRoundingDirection;
    private ResetMode resetMode;
    private DelayMode delayMode;
    private final Map<Flag, FlagMode> flagModes = new EnumMap<Flag, FlagMode>(Flag.class);
    private final DelegatingExceptionHandler exceptionHandler;

    public DynamicAttributes() {
        this.binaryRoundingDirection = DEFAULT.getBinaryRoundingDirection();
        this.decimalRoundingDirection = DEFAULT.getDecimalRoundingDirection();
        final int len = Flag.length();
        for (int i = 0; i < len; i++) {
            final Flag flag = Flag.byOrdinal(i);
            flagModes.put(flag, FlagMode.DEFAULT);
        }
        this.exceptionHandler = new DelegatingExceptionHandler();
    }
    public DynamicAttributes(final Attributes copy) {
        this.binaryRoundingDirection = Objects.requireNonNull(copy.getBinaryRoundingDirection(), "copy.getBinaryRoundingDirection() returned null");
        this.decimalRoundingDirection = Objects.requireNonNull(copy.getDecimalRoundingDirection(), "copy.getDecimalRoundingDirection() returned null");
        final int len = Flag.length();
        for (int i = 0; i < len; i++) {
            final Flag flag = Flag.byOrdinal(i);
            flagModes.put(flag, copy.getFlagMode(flag));
        }
        this.exceptionHandler = new DelegatingExceptionHandler(Objects.requireNonNull(copy.getExceptionHandler(), "copy.getExceptionHandler() returned null"));
    }
    private DynamicAttributes(final DynamicAttributes copy) {
        this.binaryRoundingDirection = copy.getBinaryRoundingDirection();
        this.decimalRoundingDirection = copy.getDecimalRoundingDirection();
        this.flagModes.putAll(copy.flagModes);
        this.exceptionHandler = copy.exceptionHandler.clone();
    }

    public static final DynamicAttributes copyOf(final Attributes attributes) {
        return attributes instanceof DynamicAttributes ? new DynamicAttributes((DynamicAttributes)attributes) : new DynamicAttributes(attributes);
    }

    @Override
    public final RoundingDirection getBinaryRoundingDirection() {
        return binaryRoundingDirection;
    }

    public void setBinaryRoundingDirection(RoundingDirection binaryRoundingDirection) {
        this.binaryRoundingDirection = Objects.requireNonNull(binaryRoundingDirection, "binaryRoundingDirection is null");
    }

    @Override
    public final RoundingDirection getDecimalRoundingDirection() {
        return decimalRoundingDirection;
    }

    public void setDecimalRoundingDirection(RoundingDirection decimalRoundingDirection) {
        this.decimalRoundingDirection = Objects.requireNonNull(decimalRoundingDirection, "decimalRoundingDirection is null");
    }

    @Override
    public ResetMode getResetMode() {
        return resetMode;
    }

    public void setResetMode(ResetMode resetMode) {
        this.resetMode = Objects.requireNonNull(resetMode, "resetMode is null");
    }

    @Override
    public final DelayMode getDelayMode() {
        return delayMode;
    }

    public void setDelayMode(DelayMode delayMode) {
        this.delayMode = Objects.requireNonNull(delayMode, "delayMode is null");
    }

    @Override
    public final FlagMode getFlagMode(final Flag flag) {
        return flagModes.get(flag);
    }

    public void setFlagMode(final Flag flag, final FlagMode flagMode) {
        Objects.requireNonNull(flag, "flag is null");
        Objects.requireNonNull(flagMode, "flagMode is null");
        this.flagModes.put(flag, flagMode);
    }

    @Override
    public final ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler.setDefaultHandler(exceptionHandler);
    }

    public void setExceptionHandler(final Flag flag, final ExceptionHandler exceptionHandler) {
        this.exceptionHandler.setDelegateHandler(flag, exceptionHandler);
    }

    public final DynamicAttributes clone() {
        return new DynamicAttributes(this);
    }
}
