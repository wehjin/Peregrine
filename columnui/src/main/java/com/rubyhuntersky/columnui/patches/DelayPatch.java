package com.rubyhuntersky.columnui.patches;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.Display;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class DelayPatch implements Patch {

    private final Frame frame;
    private final Shape shape;
    private final Display display;
    private Patch patch;
    private boolean didEndDelay;
    private boolean didRemove;

    public DelayPatch(Frame frame, Shape shape, @NonNull Display display) {

        this.frame = frame;
        this.shape = shape;
        this.display = display;
    }

    public void endDelay() {
        if (didRemove || didEndDelay) {
            return;
        }
        patch = display.addPatch(frame, shape);
        didEndDelay = true;
    }

    @Override
    public void remove() {
        if (didRemove) {
            return;
        }
        didRemove = true;
        if (patch != null) {
            patch.remove();
            patch = null;
        }
    }
}
