package com.rubyhuntersky.columnui.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.displays.DelayDisplay;
import com.rubyhuntersky.columnui.patches.DelayPatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class DelayBar extends WrapperBar implements DelayDisplay<Bar> {

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
            return original.addPatch(frame, shape);
        }
        final DelayPatch patch = new DelayPatch(frame, shape, original);
        pending.add(patch);
        return patch;
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }
}
