package com.rubyhuntersky.columnui.presentations;

import com.rubyhuntersky.columnui.Presentation;

/**
 * @author wehjin
 * @since 1/23/16.
 */
public abstract class BooleanPresentation implements Presentation {

    private boolean isCancelled;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void cancel() {
        if (!isCancelled()) {
            isCancelled = true;
            onCancel();
        }
    }

    abstract protected void onCancel();
}
