package com.rubyhuntersky.columnui.presentations;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/30/16.
 */
public class PendingPresentation extends BooleanPresentation {

    private Presentation presentation;

    public boolean isPending() {
        return presentation == null && !isCancelled();
    }

    public void setPresentation(@NonNull Presentation presentation) {
        if (this.presentation == presentation) {
            return;
        }
        if (!isPending()) {
            presentation.cancel();
            throw new IllegalStateException("PendingPresentation is no longer pending");
        }
        this.presentation = presentation;
    }

    @Override
    public float getWidth() {
        return presentation == null || isCancelled() ? 0 : presentation.getWidth();
    }

    @Override
    public float getHeight() {
        return presentation == null || isCancelled() ? 0 : presentation.getHeight();
    }

    @Override
    protected void onCancel() {
        if (presentation != null) {
            presentation.cancel();
        }
    }
}
