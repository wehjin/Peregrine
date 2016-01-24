package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presentation {
    Range getVerticalRange();
    boolean isCancelled();
    void cancel();
}
