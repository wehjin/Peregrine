package com.rubyhuntersky.gx.poles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.internal.devices.patches.Patch;
import com.rubyhuntersky.gx.internal.shapes.Shape;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.internal.devices.ShiftDisplay;
import com.rubyhuntersky.gx.internal.devices.patches.FrameShiftPatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class ShiftPole extends Pole implements ShiftDisplay<Pole> {

    private boolean didShift;
    private List<FrameShiftPatch> pending = new ArrayList<>();
    private float verticalShift;
    private float horizontalShift;

    public ShiftPole(@NonNull Pole original) {
        super(original);
    }

    @NonNull
    @Override
    public ShiftPole setShift(float horizontal, float vertical) {
        if (!didShift) {
            didShift = true;
            this.horizontalShift = horizontal;
            this.verticalShift = vertical;
            final List<FrameShiftPatch> toShift = new ArrayList<>(pending);
            pending.clear();
            for (FrameShiftPatch patch : toShift) {
                patch.setShift(horizontal, vertical);
            }
        }
        return this;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        final FrameShiftPatch patch = new FrameShiftPatch(frame, shape, basis);
        if (didShift) {
            patch.setShift(horizontalShift, verticalShift);
        } else {
            pending.add(patch);
        }
        return patch;
    }
}
