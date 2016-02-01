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

import java.util.Collection;
import java.util.EnumSet;

/**
 * Utility class with static methods to convert sets of {@link Flag flags} represented as
 * an int (as used in {@link Flags}) from and to {@link Collection collections}.
 */
public final class FlagUtil {

    /**
     * Converts the given flag set to an {@link EnumSet}. All bits in {@code flags} which are not
     * associated with a {@link Flag} are ignored.
     *
     * @param flags an int representing the set of flags
     * @return an enum set containing the flags which are associated with a 1-bit in {@code flags}
     */
    public static final EnumSet<Flag> toSet(final int flags) {
        final EnumSet<Flag> set = EnumSet.noneOf(Flag.class);
        final int len = Flag.length();
        for (int i = 0; i < len; i++) {
            final Flag flag = Flag.byOrdinal(i);
            if (flag.test(flags)) {
                set.add(flag);
            }
        }
        return set;
    }

    /**
     * Converts the given flag collection to an int set.
     *
     * @param flags a collection of flags
     * @return an int set with 1-bits associated with flags that are found in {@code flags}
     */
    public static final int from(final Collection<? extends Flag> flags) {
        int intFlags = 0;
        final int len = flags.isEmpty() ? 0 : Flag.length();
        for (int i = 0; i < len; i++) {
            final Flag flag = Flag.byOrdinal(i);
            if (flags.contains(flag)) {
                intFlags = flag.set(intFlags);
            }
        }
        return intFlags;
    }

    /**
     * Converts the given flags to an int set.
     *
     * @param flags a vararg list of flags
     * @return an int set with 1-bits associated with flags that are found in {@code flags}
     */
    public static final int from(final Flag... flags) {
        int intFlags = 0;
        final int len = flags.length;
        for (int i = 0; i < len; i++) {
            intFlags = flags[i].set(intFlags);
        }
        return intFlags;
    }

    private FlagUtil() {
        throw new IllegalStateException("No FlagUtil for you!");
    }
}
