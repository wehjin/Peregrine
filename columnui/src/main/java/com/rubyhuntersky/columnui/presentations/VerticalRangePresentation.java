package com.rubyhuntersky.columnui.presentations;

import com.rubyhuntersky.columnui.Presentation;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class VerticalRangePresentation implements Presentation {

    private final float width;
    private final float height;
    private final Presentation original;

    public VerticalRangePresentation(float width, float height, Presentation original) {
        this.width = width;
        this.height = height;
        this.original = original;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public boolean isCancelled() {
        return original.isCancelled();
    }

    @Override
    public void cancel() {
        original.cancel();
    }
}
