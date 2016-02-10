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

final class DelegatingExceptionHandler implements ExceptionHandler, Cloneable {
    private ExceptionHandler defaultHandler;
    private final Map<Flag, ExceptionHandler> delegateHandlers = new EnumMap<Flag, ExceptionHandler>(Flag.class);

    public DelegatingExceptionHandler() {
        this.defaultHandler = ExceptionHandler.DEFAULT;
    }
    public DelegatingExceptionHandler(final ExceptionHandler defaultHandler) {
        this.defaultHandler = Objects.requireNonNull(defaultHandler, "defaultHandler is null");
    }
    private DelegatingExceptionHandler(final DelegatingExceptionHandler copy) {
        this.defaultHandler = copy.defaultHandler;
        this.delegateHandlers.putAll(copy.delegateHandlers);
    }

    private ExceptionHandler handlerFor(final Flag flag) {
        final ExceptionHandler handler = delegateHandlers.get(flag);
        return handler != null ? handler : defaultHandler;
    }

    public void setDefaultHandler(ExceptionHandler defaultHandler) {
        this.defaultHandler = Objects.requireNonNull(defaultHandler, "defaultHandler is null");
    }

    public void setDelegateHandler(final Flag flag, final ExceptionHandler exceptionHandler) {
        Objects.requireNonNull(flag, "flag is null");
        Objects.requireNonNull(exceptionHandler, "exceptionHandler is null");
        delegateHandlers.put(flag, exceptionHandler);
    }

    public void removeDelegateHandlers() {
        delegateHandlers.clear();
    }

    @Override
    public long handleException(final String operation, final long a, final long b, final long result, final Flag flag, final Flag otherFlag, final Attributes attributes) {
        return handlerFor(flag).handleException(operation, a, b, result, flag, otherFlag, attributes);
    }

    public DelegatingExceptionHandler clone() {
        return new DelegatingExceptionHandler(this);
    }
}
