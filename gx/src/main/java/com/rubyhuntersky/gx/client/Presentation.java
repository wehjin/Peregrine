package com.rubyhuntersky.gx.client;

import com.rubyhuntersky.gx.internal.presentations.EmptyPresentation;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presentation {
    Presentation EMPTY = new EmptyPresentation();

    float getWidth();
    float getHeight();
    boolean isCancelled();
    void cancel();
}
