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

    public Column(@NonNull Range horizontalRange) {
        this.horizontalRange = horizontalRange;
    }

    @NonNull
    abstract public Patch addPatch(Frame frame, Shape shape, Coloret color);

    public Column withHorizontalRange(Range range) {
        final Column original = this;
        return new Column(range) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret color) {
                return original.addPatch(frame, shape, color);
            }
        };
    }
}
