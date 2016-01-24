package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Range {

    public static final Range ZERO = new Range(0, 0);

    public static Range of(float start, float end) {
        return new Range(start, end);
    }

    public final float start;
    public final float end;

    public Range(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public float toLength() {
        return end - start;
    }

    public Range union(Range range) {
        float newStart = Math.min(start, range.start);
        float newEnd = Math.max(end, range.end);
        if (isEqual(newStart, newEnd)) {
            return this;
        } else if (range.isEqual(newStart, newEnd)) {
            return range;
        } else {
            return new Range(newStart, newEnd);
        }
    }

    public Range inset(float value) {
        if (value == 0) {
            return this;
        }
        float newStart = start + value;
        float newEnd = end - value;
        return new Range(newStart, newEnd);
    }

    private boolean isEqual(float start, float end) {
        return this.start == start && this.end == end;
    }
}
