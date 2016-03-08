package com.rubyhuntersky.gx.internal.presentations;

import com.rubyhuntersky.gx.client.Presentation;

/**
 * @author wehjin
 * @since 1/30/16.
 */
public class EmptyPresentation implements Presentation {
    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public boolean isCancelled() {
        return true;
    }

    @Override
    public void cancel() {
        // Do nothing
    }
}
