package com.rubyhuntersky.gx.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.patches.Patch;
import com.rubyhuntersky.gx.Shape;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.displays.DelayDisplay;
import com.rubyhuntersky.gx.patches.DelayPatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class DelayColumn extends Column implements DelayDisplay<Column> {

    protected boolean didEndDelay;
    private List<DelayPatch> pending = new ArrayList<>();

    public DelayColumn(@NonNull Column original) {
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
