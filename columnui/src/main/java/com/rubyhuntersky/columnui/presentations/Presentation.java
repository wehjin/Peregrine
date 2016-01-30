package com.rubyhuntersky.columnui.presentations;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presentation {
    Presentation EMPTY = new CancelledPresentation();

    float getWidth();
    float getHeight();
    boolean isCancelled();
    void cancel();
}
