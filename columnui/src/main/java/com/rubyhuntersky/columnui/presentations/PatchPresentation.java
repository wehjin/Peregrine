package com.rubyhuntersky.columnui.presentations;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.basics.Frame;

/**
 * @author wehjin
 * @since 1/28/16.
 */
public class PatchPresentation extends BooleanPresentation {
    private final Frame frame;
    private final Patch patch;

    public PatchPresentation(Patch patch, Frame frame) {
        this.frame = frame;
        this.patch = patch;
    }

    @Override
    public float getWidth() {
        return frame.horizontal.toLength();
    }

    @Override
    public float getHeight() {
        return frame.vertical.toLength();
    }

    @Override
    protected void onCancel() {
        patch.remove();
    }
}
