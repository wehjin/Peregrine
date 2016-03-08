package com.rubyhuntersky.gx.poles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.internal.devices.patches.Patch;
import com.rubyhuntersky.gx.internal.shapes.Shape;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.internal.devices.DelayDisplay;
import com.rubyhuntersky.gx.internal.devices.patches.DelayPatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class DelayPole extends Pole implements DelayDisplay<Pole> {

    protected boolean didEndDelay;
    private List<DelayPatch> pending = new ArrayList<>();

    public DelayPole(@NonNull Pole original) {
        super(original);
    }

    @Override
    public void endDelay() {
        if (didEndDelay) {
            return;
        }
        didEndDelay = true;
        final List<DelayPatch> toEndDelay = new ArrayList<>(pending);
        pending.clear();
        for (DelayPatch patch : toEndDelay) {
            patch.endDelay();
        }
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        if (didEndDelay) {
            return super.addPatch(frame, shape);
        }
        final DelayPatch patch = new DelayPatch(frame, shape, basis);
        pending.add(patch);
        return patch;
    }
}
