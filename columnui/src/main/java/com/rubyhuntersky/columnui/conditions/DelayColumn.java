package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

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
public class DelayColumn extends Column {

    @NonNull
    private final Column original;
    private boolean didEndDelay;
    private List<DelayPatch> pending = new ArrayList<>();

    public DelayColumn(@NonNull Column original) {
        super(original.horizontalRange, original.verticalRange, original.elevation);
        this.original = original;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        final DelayPatch patch = new DelayPatch(frame, shape, original);
        if (didEndDelay) {
            patch.endDelay();
        } else {
            pending.add(patch);
        }
        return patch;
    }

    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }

    public void endDelay() {
        if (didEndDelay) {
            throw new IllegalStateException("Delay already ended");
        }
        didEndDelay = true;
        final List<DelayPatch> toShift = new ArrayList<>(pending);
        pending.clear();
        for (DelayPatch patch : toShift) {
            patch.endDelay();
        }
    }
}
