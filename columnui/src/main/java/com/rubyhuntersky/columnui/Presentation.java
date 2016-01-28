package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.basics.Range;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presentation {
    Range getVerticalRange();
    boolean isCancelled();
    void cancel();
}
