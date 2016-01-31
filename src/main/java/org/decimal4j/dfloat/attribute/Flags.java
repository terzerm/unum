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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * A set of {@link Flag}.
 */
public class Flags {

    private static ThreadLocal<int[]> FLAGS = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[1];
        }
    };

    public static int saveAllFlags() {
        return FLAGS.get()[0];
    }

    public static void restoreFlags(final int flags) {
        FLAGS.get()[0] = flags & Flag.ALL;
    }

    public static void restoreFlag(final int flags, final Flag flag) {
        FLAGS.get()[0] = flags & flag.ordinal();
    }
    public static void restoreFlags(final int flags, final int exceptionGroup) {
        FLAGS.get()[0] = flags & Flag.ALL & exceptionGroup;
    }

    public static boolean testFlag(final Flag flag) {
        return testSavedFlag(saveAllFlags(), flag);
    }

    public static boolean testFlags() {
        return testSavedFlags(saveAllFlags());
    }

    public static boolean testFlags(final int exceptionGroup) {
        return testSavedFlags(saveAllFlags(), exceptionGroup);
    }

    public static boolean testSavedFlag(final int flags, final Flag flag) {
        return flag.test(flags);
    }
    public static boolean testSavedFlags(final int flags) {
        return testSavedFlags(flags, Flag.ALL);
    }
    public static boolean testSavedFlags(final int flags, final int exceptionGroup) {
        return (flags & exceptionGroup) != 0;
    }

    public static void lowerFlag(final Flag flag) {
        FLAGS.get()[0] &= (Flag.ALL - flag.ordinal());
    }
    public static void lowerFlags() {
        FLAGS.get()[0] = Flag.NONE;
    }
    public static void lowerFlags(final int exceptionGroup) {
        FLAGS.get()[0] &= (Flag.ALL - (exceptionGroup & Flag.ALL));
    }

    public static void raiseFlag(final Flag flag) {
        FLAGS.get()[0] |= flag.ordinal();
    }
    public static void raiseFlags(final int exceptionGroup) {
        FLAGS.get()[0] |= (Flag.ALL & exceptionGroup);
    }

}
