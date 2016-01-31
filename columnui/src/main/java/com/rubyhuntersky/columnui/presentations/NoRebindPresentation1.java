package com.rubyhuntersky.columnui.presentations;

/**
 * @author wehjin
 * @since 1/30/16.
 */
public class NoRebindPresentation1<C> implements Presentation1<C> {
    private final Presentation present;

    public NoRebindPresentation1(Presentation present) {
        this.present = present;
    }

    @Override
    public void rebind(C condition) {
        // Do nothing
    }

    @Override
    public float getWidth() {
        return present.getWidth();
    }

    @Override
    public float getHeight() {
        return present.getHeight();
    }

    @Override
    public boolean isCancelled() {
        return present.isCancelled();
    }

    @Override
    public void cancel() {
        present.cancel();
    }
}
