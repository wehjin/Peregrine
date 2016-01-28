package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.Range;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.conditions.DelayColumn;
import com.rubyhuntersky.columnui.conditions.DelayedVerticalShiftColumn;

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

    public Column(@NonNull Range horizontalRange, @NonNull Range verticalContext, int elevation) {
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalContext;
        this.elevation = elevation;
    }

    public float getWidth() {
        return horizontalRange.toLength();
    }

    @NonNull
    abstract public Patch addPatch(Frame frame, Shape shape);

    abstract public TextSize measureText(String text, TextStyle textStyle);


    public Column withElevation(int elevation) {
        return this.elevation == elevation ? this : new ColumnWrapper(horizontalRange, verticalRange, elevation, this);
    }

    public Column withHorizontalRange(Range range) {
        return this.horizontalRange == range ? this : new ColumnWrapper(range, verticalRange, elevation, this);
    }

    public Column withVerticalRange(Range range) {
        return this.verticalRange == range ? this : new ColumnWrapper(horizontalRange, range, elevation, this);
    }

    public DelayedVerticalShiftColumn withDelayedVerticalShift() {
        return new DelayedVerticalShiftColumn(this);
    }

    public DelayColumn withDelay() {
        return new DelayColumn(this);
    }
}
