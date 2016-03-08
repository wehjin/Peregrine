package com.rubyhuntersky.gx.displays;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public interface ShiftDisplay<T> extends CoreDisplay<T> {

    @NonNull
    ShiftDisplay<T> setShift(float horizontal, float vertical);
}
