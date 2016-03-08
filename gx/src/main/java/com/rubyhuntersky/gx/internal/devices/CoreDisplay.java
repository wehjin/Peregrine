package com.rubyhuntersky.gx.internal.devices;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/28/16.
 */

public interface CoreDisplay<T> extends PatchDevice {
    @NonNull
    T asType();

    @NonNull
    T withElevation(int elevation);

    @NonNull
    DelayDisplay<T> withDelay();

    @NonNull
    ShiftDisplay<T> withShift();
}
