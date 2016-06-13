/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 tools4j-unum, Marco Terzer
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
package org.tools4j.unum.api;

public final class DefaultUbound<U extends Unum<U>> implements Ubound<U> {

    private final U lower;
    private final U upper;

    DefaultUbound(final U lower, final U upper) {
        if (lower.compareTo(upper) > 0) {
            throw new IllegalArgumentException("lower is larger than upper: " + lower + " > " + upper);
        }
        this.lower = lower;
        this.upper = upper;
    }

    public final U getLowerBound() {
        return lower;
    }

    public final U getUpperBound() {
        return upper;
    }

    public final Ubound<U> width() {
        if (lower.equals(upper)) {
            return Ubound.create(lower.intervalWidth());
        }
        throw new RuntimeException("not implemented");//FIXME
    }

    public final Boundary boundary() {
        return isLowerClosed() ?
                (isUpperClosed() ? Boundary.CLOSED_CLOSED : Boundary.CLOSED_OPEN) :
                (isUpperClosed() ? Boundary.OPEN_CLOSED : Boundary.OPEN_OPEN);
    }
    public final Overlap overlap(final Ubound<U> other) {
        if (isNaN() || other.isNaN()) {
            return Overlap.EMPTY;
        }
        if (this == other) {
            return Overlap.EQUAL;
        }
        final Ubound<U> minUpper = getUpperBound().compareTo(other.getUpperBound()) <= 0 ? this : other;
        final Ubound<U> maxLower = getLowerBound().compareTo(other.getLowerBound()) >= 0 ? this : other;
        final int cmp = minUpper.getUpperBound().compareTo(maxLower.getLowerBound());
        if (cmp < 0) {
            return Overlap.APART;
        } else if (cmp == 0) {
            if (minUpper.getUpperBound().isInexact() || maxLower.getLowerBound().isInexact()) {
                return Overlap.NEARLY_TOUCHING;
            }
            return (minUpper.isSinglePoint() || maxLower.isSinglePoint()) ? Overlap.CONTAINING : Overlap.TOUCHING;
        }

        if (minUpper == maxLower) {
            final Ubound<U> inner = minUpper;
            final Ubound<U> outer = inner == this ? other : this;
            if (inner.getLowerBound().equals(outer.getLowerBound()) && inner.getUpperBound().equals(outer.getUpperBound())) {
                return Overlap.EQUAL;
            }
            return Overlap.CONTAINING;
        }
        return Overlap.OVERLAPPING;
    }
    public final boolean isNowhereEqualTo(final Ubound<U> other) {
        if (isNaN() || other.isNaN()) {
            return true;
        }
        if (this == other) {
            return false;
        }
        final U minUpper = getUpperBound().compareTo(other.getUpperBound()) <= 0 ? getUpperBound() : other.getUpperBound();
        final U maxLower = getLowerBound().compareTo(other.getLowerBound()) >= 0 ? getLowerBound() : other.getLowerBound();
        final int cmp = minUpper.compareTo(maxLower);
        return (cmp < 0 || (cmp == 0 && (minUpper.isInexact() || maxLower.isInexact())));
    }
    public final boolean isSomewhereEqualTo(final Ubound<U> other) {
        return !isNowhereEqualTo(other);
    }
    public final boolean isEverywhereEqualTo(final Ubound<U> other) {
        if (isNaN() || other.isNaN()) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return getLowerBound().equals(other.getLowerBound()) && getUpperBound().equals(other.getUpperBound());
    }
    public final Ubound<U> intersect(final Ubound<U> with) {
        if (this == with || isNaN()) {
            return this;
        }
        if (with.isNaN()) {
            return with;
        }
        final U minUpper = getUpperBound().compareTo(with.getUpperBound()) <= 0 ? getUpperBound() : with.getUpperBound();
        final U maxLower = getLowerBound().compareTo(with.getLowerBound()) >= 0 ? getLowerBound() : with.getLowerBound();
        final int cmp = minUpper.compareTo(maxLower);
        if (cmp < 0 || (cmp == 0 && (minUpper.isInexact() || maxLower.isInexact()))) {
            //nowhere equal
            return getFactory().qNaN();
        }
        return Ubound.create(maxLower, minUpper);
    }
    public final Ubound<U> span(final Ubound<U> with) {
        if (this == with) {
            return this;
        }
        if (isNaN()) {
            return with;
        }
        if (with.isNaN()) {
            return this;
        }
        final U minLower = getLowerBound().compareTo(with.getLowerBound()) <= 0 ? getLowerBound() : with.getLowerBound();
        final U maxUpper = getUpperBound().compareTo(with.getUpperBound()) >= 0 ? getUpperBound() : with.getUpperBound();
        return Ubound.create(minLower, maxUpper);
    }

    @Override
    public int hashCode() {
        return 31 * lower.hashCode() + upper.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (getClass() == obj.getClass()) {
            return isEverywhereEqualTo((Ubound<U>)obj);
        }
        return false;
    }

    @Override
    public String toString() {
        if (isNaN()) {
            return "(qNaN)";
        }
        final StringBuilder sb = new StringBuilder();
        final boolean lowerExact = lower.isExact();
        if (lowerExact) {
            sb.append('[');
        } else {
            sb.append('(');
        }
        sb.append(lower.getLowerBound());
        if (lowerExact && lower.equals(upper)) {
            sb.append(']');
        } else {
            sb.append(", ");
            sb.append(upper.getUpperBound());
            if (upper.isExact()) {
                sb.append(']');
            } else {
                sb.append(')');
            }
        }
        return sb.toString();
    }
}
