package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Column;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class VerticalShiftPatch implements Patch {

    private final Frame frame;
    private final Shape shape;
    private final Column column;
    private Patch patch;

    public VerticalShiftPatch(Frame frame, Shape shape, @NonNull Column column) {

        this.frame = frame;
        this.shape = shape;
        this.column = column;
    }

    public void setVerticalShift(float shift) {
        if (patch != null) {
            throw new IllegalStateException("Already shifted");
        }
        Frame newFrame = this.frame.withVerticalShift(shift);
        patch = column.addPatch(newFrame, shape);
    }

    @Override
    public void remove() {
        if (patch != null) {
            patch.remove();
            patch = null;
        }
    }
}
