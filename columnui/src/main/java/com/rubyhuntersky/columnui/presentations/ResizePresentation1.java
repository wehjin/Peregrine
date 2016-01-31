package com.rubyhuntersky.columnui.presentations;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public class ResizePresentation1<C> implements Presentation1<C> {

    private final float width;
    private final float height;
    private final Presentation1<C> original;

    public ResizePresentation1(float width, float height, Presentation1<C> original) {
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

    @Override
    public void rebind(C condition) {
        original.rebind(condition);
    }
}
