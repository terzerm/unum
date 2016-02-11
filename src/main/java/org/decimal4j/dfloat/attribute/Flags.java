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

/**
 * Contains methods to set, test, save and restore {@link Flag flags}. A set of Flags is
 * represented as an int to minimise gargage during calculations. Flags are associated with
 * a thread (stored by a {@link ThreadLocal}).
 */
public class Flags {

    /** An empty set of flags with no flags raised */
    public static final int NONE = 0;

    /** A set of flags with all flags raised */
    public static final int ALL = (1 << Flag.length()) - 1;

    /** Flags associated with the current thread*/
    private static ThreadLocal<int[]> FLAGS = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[]{NONE};
        }
    };

    /**
     * Returns the flags associated with the current thread.
     * @return an int representing the set of flags
     */
    public static int saveAllFlags() {
        return FLAGS.get()[0];
    }

    /**
     * Restores the flags associated with the current thread to {@code flags}.
     * Bits in the int which are not associated with a flag are ignored.
     *
     * @param flags an int representing the set of flags
     */
    public static void restoreFlags(final int flags) {
        FLAGS.get()[0] = flags & ALL;
    }

    /**
     * Restores the specified {@code flag} to its state represented in {@code flags}.
     * <p>
     * All other flags are not changed.
     *
     * @param flags an int representing the set of flags
     * @param flag the flag to restore
     */
    public static void restoreFlag(final int flags, final Flag flag) {
        final int[] f = FLAGS.get();
        f[0] &= flag.clear(ALL);
        f[0] |= (flag.set(NONE) & flags);
    }

    /**
     * Restores the flags corresponding to the exceptions specified in {@code exceptionGroup},
     * which can represent any subset of the exceptions, to their state represented in {@code flags}.
     * <p>
     * Bits in {@code exceptionGroup} which are not associated with an exception flag are ignored.
     * Flags associated with 0-bits in {@code exceptionGroup} are not changed.
     *
     * @param flags an int representing the set of flags
     * @param exceptionGroup an int representing the subset exceptions to restore
     */
    public static void restoreFlags(final int flags, final int exceptionGroup) {
        final int[] f = FLAGS.get();
        f[0] &= (ALL - (exceptionGroup & ALL));
        f[0] |= (flags & exceptionGroup & ALL);
    }

    /**
     * Queries whether the specified {@code flag} is raised.
     *
     * @param flag the exception flag to test
     * @return  true if the specified flag is raised
     */
    public static boolean testFlag(final Flag flag) {
        return testSavedFlag(saveAllFlags(), flag);
    }

    /**
     * Queries whether any exception flag is raised.
     *
     * @return  true if any flag is raised
     */
    public static boolean testFlags() {
        return testSavedFlags(saveAllFlags());
    }

    /**
     * Queries whether any of the flags corresponding to the exceptions specified in {@code exceptionGroup},
     * which can represent any subset of the exceptions, are raised.
     * <p>
     * Bits in {@code exceptionGroup} which are not associated with an exception flag are ignored.
     * Flags associated with 0-bits in {@code exceptionGroup} are not considered.
     *
     * @param exceptionGroup an int representing the subset exceptions to test
     * @return  true if any of the flags corresponding to the exceptions specified in {@code exceptionGroup}
     *          is raised
     */
    public static boolean testFlags(final int exceptionGroup) {
        return testSavedFlags(saveAllFlags(), exceptionGroup);
    }

    /**
     * Queries whether the specified {@code flag} is raised in {@code flags}.
     *
     * @param flags the set of saved flags to test
     * @param flag  the flag to test
     * @return true if {@code flag} is raised in {@code flags}
     */
    public static boolean testSavedFlag(final int flags, final Flag flag) {
        return flag.test(flags);
    }

    /**
     * Queries whether any of the flags in the {@code flags} operand are raised.
     * <p>
     * Bits in {@code flags} which are not associated with an exception flag are ignored.
     *
     * @param flags          the set of saved flags to test
     * @return true if any flag is raised in {@code flags}
     */
    public static boolean testSavedFlags(final int flags) {
        return testSavedFlags(flags, ALL);
    }

    /**
     * Queries whether any of the flags in the {@code flags} operand corresponding to the exceptions specified in
     * {@code exceptionGroup}, which can represent any subset of the exceptions, are raised.
     * <p>
     * Bits in {@code flags} and {@code exceptionGroup} which are not associated with an exception flag are ignored.
     * Flags associated with 0-bits in {@code exceptionGroup} are not considered.
     *
     * @param flags          the set of saved flags to test
     * @param exceptionGroup an int representing the subset exceptions to test
     * @return  true if any of the flags corresponding to the exceptions specified in {@code exceptionGroup}
     *          is raised in {@code flags}
     */
    public static boolean testSavedFlags(final int flags, final int exceptionGroup) {
        return (flags & exceptionGroup & ALL) != NONE;
    }

    /**
     * Lowers (clears) the specified flag. All other flags are not changed.
     *
     * @param flag the flag to lower
     */
    public static void lowerFlag(final Flag flag) {
        FLAGS.get()[0] &= flag.clear(ALL);
    }

    /**
     * Lowers (clears) all flags.
     */
    public static void lowerFlags() {
        FLAGS.get()[0] = NONE;
    }

    /**
     * Lowers (clears) the flags corresponding to the exceptions specified in {@code exceptionGroup},
     * which can represent any subset of the exceptions.
     * <p>
     * Bits in {@code exceptionGroup} which are not associated with an exception flag are ignored.
     * Flags associated with 0-bits in {@code exceptionGroup} are not changed.
     *
     * @param exceptionGroup an int representing the subset exceptions to raise
     */
    public static void lowerFlags(final int exceptionGroup) {
        FLAGS.get()[0] &= (ALL - (exceptionGroup & ALL));
    }

    /**
     * Raises (sets) the specified flag. All other flags are not changed.
     *
     * @param flag the flag to raise
     */
    public static void raiseFlag(final Flag flag) {
        FLAGS.get()[0] |= flag.set(NONE);
    }

    /**
     * Raises (sets) the flags corresponding to the exceptions specified in {@code exceptionGroup},
     * which can represent any subset of the exceptions.
     * <p>
     * Bits in {@code exceptionGroup} which are not associated with an exception flag are ignored.
     * Flags associated with 0-bits in {@code exceptionGroup} are not changed.
     *
     * @param exceptionGroup an int representing the subset exceptions to raise
     */
    public static void raiseFlags(final int exceptionGroup) {
        FLAGS.get()[0] |= (exceptionGroup & ALL);
    }

    /**
     * Resets all flags if and only if {@code resetMode} equals {@link ResetMode#Reset Reset}.
     *
     * @param resetMode the mode determining whether or not to reset all flags
     */
    public static void resetFlags(final ResetMode resetMode) {
        if (resetMode == ResetMode.Reset) {
            lowerFlags();
        }
    }

}
