package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public interface FixedDisplay<T> extends CoreDisplay<T> {

    @NonNull
    T withFixedDimension(float fixedDimension);
}
