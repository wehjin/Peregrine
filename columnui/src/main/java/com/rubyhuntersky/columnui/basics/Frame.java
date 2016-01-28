package com.rubyhuntersky.columnui.basics;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Frame {
    public final Range horizontal;
    public final Range vertical;
    public final int elevation;

    public Frame(Range horizontal, Range vertical, int elevation) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.elevation = elevation;
    }

    public Frame withVerticalShift(float shift) {
        Range newVertical = vertical.shift(shift);
        if (newVertical == vertical) {
            return this;
        }
        return new Frame(horizontal, newVertical, elevation);
    }

    public Frame withVertical(Range range) {
        return new Frame(horizontal, range, elevation);
    }

    public Frame withVerticalLength(float verticalLength) {
        final Range newVertical = vertical.withLength(verticalLength);
        return new Frame(horizontal, newVertical, elevation);
    }
}
