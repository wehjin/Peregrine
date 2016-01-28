package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presentation {
    float getWidth();
    float getHeight();
    boolean isCancelled();
    void cancel();
}
