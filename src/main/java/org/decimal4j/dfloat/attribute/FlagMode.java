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

public enum FlagMode {
    /**
     * Default exception handling, which is to deliver a default result, continue
     * execution, and raise the corresponding status flag.
     */
    RaiseFlag,
    /**
     * Deliver default exception result without raising the corresponding status flag.
     */
    RaiseNoFlag,
    /**
     * Deliver default exception result only raising the status flag ... FIXME
     */
    MayRaiseFlag,
    /**
     * Deliver the default exception result and record the corresponding exception whenever a flag is raised. Recording
     * an exception means storing a description of the exception, including language-standard-defined details which
     * might include the current operation and operands, and the location of the exception. Language standards define
     * operations to convert exception descriptions to and from character sequences, and to inspect, save, and restore
     * exception descriptions.
     */
    RecordException;

    public static final FlagMode DEFAULT = RaiseFlag;
}