package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class VerticalShiftPatch implements Patch {

    private final Frame frame;
    private final Shape shape;
    private final Coloret color;
    private final Column column;
    private Patch patch;

    public VerticalShiftPatch(Frame frame, Shape shape, Coloret color, @NonNull Column column) {

        this.frame = frame;
        this.shape = shape;
        this.color = color;
        this.column = column;
    }

    public void setVerticalShift(float shift) {
        if (patch != null) {
            throw new IllegalStateException("Already shifted");
        }
        Frame newFrame = this.frame.withVerticalShift(shift);
        patch = column.addPatch(newFrame, shape, color);
    }

    @Override
    public void remove() {
        if (patch != null) {
            patch.remove();
            patch = null;
        }
    }
}
