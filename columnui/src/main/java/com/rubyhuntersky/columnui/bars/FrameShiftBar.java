package com.rubyhuntersky.columnui.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.patches.FrameShiftPatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class FrameShiftBar extends WrapperBar {

    @NonNull
    private boolean didShift;
    private List<FrameShiftPatch> pending = new ArrayList<>();
    private float verticalShift;
    private float horizontalShift;

    public FrameShiftBar(@NonNull Bar original) {
        super(original);
    }

    public FrameShiftBar setShift(float horizontalShift, float verticalShift) {
        if (!didShift) {
            didShift = true;
            this.horizontalShift = horizontalShift;
            this.verticalShift = verticalShift;
            final List<FrameShiftPatch> toShift = new ArrayList<>(pending);
            pending.clear();
            for (FrameShiftPatch patch : toShift) {
                patch.setShift(horizontalShift, verticalShift);
            }
        }
        return this;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        final FrameShiftPatch patch = new FrameShiftPatch(frame, shape, original);
        if (didShift) {
            patch.setShift(horizontalShift, verticalShift);
        } else {
            pending.add(patch);
        }
        return patch;
    }
}
