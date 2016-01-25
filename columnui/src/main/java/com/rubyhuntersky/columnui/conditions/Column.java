package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.ColumnWrapper;
import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Range;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.TextSize;
import com.rubyhuntersky.columnui.TextStyle;

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
