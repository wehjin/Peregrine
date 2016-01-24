package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Range;
import com.rubyhuntersky.columnui.Shape;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Column extends Condition {

    @NonNull
    final public Range horizontalRange;
    @NonNull
    final public Range verticalRange;
    final public int elevation;

    public Column(@NonNull Range horizontalRange, @NonNull Range verticalRange, int elevation) {
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
        this.elevation = elevation;
    }

    @NonNull
    abstract public Patch addPatch(Frame frame, Shape shape, Coloret color);

    public Column withElevation(int elevation) {
        if (elevation == this.elevation) return this;

        final Column original = this;
        return new Column(horizontalRange, verticalRange, elevation) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }

    public Column withHorizontalRange(Range range) {
        final Column original = this;
        if (original.horizontalRange == range) {
            return this;
        }
        return new Column(range, verticalRange, elevation) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }

    public Column withVerticalRange(Range range) {
        final Column original = this;
        if (original.verticalRange == range) {
            return this;
        }
        return new Column(horizontalRange, range, elevation) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }

    public VerticalShiftColumn withVerticalShift() {
        return new VerticalShiftColumn(this);
    }

    public DelayColumn withDelay() {
        return new DelayColumn(this);
    }
}
