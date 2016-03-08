package com.rubyhuntersky.gx.patches;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.Shape;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.displays.ShapeDisplay;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class FrameShiftPatch implements Patch {

    private final Frame frame;
    private final Shape shape;
    private final ShapeDisplay display;
    private Patch patch;
    private boolean didShift;
    private boolean didRemove;

    public FrameShiftPatch(Frame frame, Shape shape, @NonNull ShapeDisplay display) {
        this.frame = frame;
        this.shape = shape;
        this.display = display;
    }

    public void setShift(float horizontal, float vertical) {
        if (didRemove || didShift) {
            return;
        }
        didShift = true;
        patch = display.addPatch(frame.withShift(horizontal, vertical), shape);
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
