package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public interface FrameShiftDisplay<T> extends CoreDisplay<T> {

    @NonNull
    FrameShiftDisplay<T> setShift(float horizontal, float vertical);
}
