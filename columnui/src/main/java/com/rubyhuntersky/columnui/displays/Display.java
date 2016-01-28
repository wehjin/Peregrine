package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public interface Display<T> extends CoreDisplay<T> {

    @NonNull
    T asType();

    @NonNull
    T withFixedDimension(float fixedDimension);

    @NonNull
    T withElevation(int elevation);

    @NonNull
    DelayDisplay<T> withDelay();
}
