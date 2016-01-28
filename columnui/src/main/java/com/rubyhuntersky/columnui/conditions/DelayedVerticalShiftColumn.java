package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Column;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class DelayedVerticalShiftColumn extends Column {

    @NonNull
    private final Column original;
    private boolean didShift;
    private float shift;
    private List<VerticalShiftPatch> pending = new ArrayList<>();

    public DelayedVerticalShiftColumn(@NonNull Column original) {
        super(original.horizontalRange, original.verticalRange, original.elevation);
        this.original = original;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        final VerticalShiftPatch patch = new VerticalShiftPatch(frame, shape, original);
        if (didShift) {
            patch.setVerticalShift(shift);
        } else {
            pending.add(patch);
        }
        return patch;
    }

    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }

    public void setVerticalShift(float shift) {
        if (didShift) {
            throw new IllegalStateException("Vertical shift already present");
        }
        didShift = true;
        this.shift = shift;
        final List<VerticalShiftPatch> toShift = new ArrayList<>(pending);
        pending.clear();
        for (VerticalShiftPatch patch : toShift) {
            patch.setVerticalShift(shift);
        }
    }
}
