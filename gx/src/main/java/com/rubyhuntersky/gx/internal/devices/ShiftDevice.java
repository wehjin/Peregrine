package com.rubyhuntersky.gx.internal.devices;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/24/16.
 */
public interface ShiftDevice<T> extends CoreDevice<T> {

    @NonNull
    ShiftDevice<T> setShift(float horizontal, float vertical);
}
