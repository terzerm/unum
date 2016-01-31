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

import java.util.*;

public enum Flag {
    InvalidOperation,
    DivisionByZero,
    Overflow,
    Underflow,
    Inexact;

    public static final int NONE = 0;
    public static final int ALL = (1 << length()) - 1;

    private final int mask = 1 << ordinal();

    public final boolean test(final int flags) {
        return (flags & mask) != 0;
    }
    public final int set(final int flags) {
        return flags | mask;
    }
    public final int clear(final int flags) {
        return flags & (~mask);
    }

    public static final EnumSet<Flag> toSet(final int flags) {
        final EnumSet<Flag> set = EnumSet.noneOf(Flag.class);
        final int len = UNIVERSE.length;
        for (int i = 0; i < len; i++) {
            final Flag flag = UNIVERSE[i];
            if (flag.test(flags)) {
                set.add(flag);
            }
        }
        return set;
    }

    public static final int setAll(final Collection<? extends Flag> flags) {
        int intFlags = 0;
        final int len = flags.isEmpty() ? 0 : UNIVERSE.length;
        for (int i = 0; i < len; i++) {
            final Flag flag = UNIVERSE[i];
            if (flags.contains(flag)) {
                intFlags = flag.set(intFlags);
            }
        }
        return intFlags;
    }

    public static final int setAll(final Flag... flags) {
        int intFlags = 0;
        final int len = flags.length;
        for (int i = 0; i < len; i++) {
            intFlags = flags[i].set(intFlags);
        }
        return intFlags;
    }

    public static final Flag byOrdinal(final int ordinal) {
        return UNIVERSE[ordinal];
    }

    public static final int length() {
        return UNIVERSE.length;
    }

    private static final Flag[] UNIVERSE = Flag.values();
}
