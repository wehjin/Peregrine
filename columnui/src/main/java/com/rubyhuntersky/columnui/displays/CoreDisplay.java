package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/28/16.
 */

public interface CoreDisplay<T> extends ShapeDisplay {
    @NonNull
    T asType();

    @NonNull
    T withElevation(int elevation);

    @NonNull
    DelayDisplay<T> withDelay();

    @NonNull
    FrameShiftDisplay<T> withShift();
}
