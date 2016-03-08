package com.rubyhuntersky.gx.bars;

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
public class DelayBar extends Bar implements DelayDisplay<Bar> {

    protected boolean didEndDelay;
    private List<DelayPatch> pending = new ArrayList<>();

    public DelayBar(@NonNull Bar original) {
        super(original);
    }

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
            return basis.addPatch(frame, shape);
        }
        final DelayPatch patch = new DelayPatch(frame, shape, basis);
        pending.add(patch);
        return patch;
    }
}
