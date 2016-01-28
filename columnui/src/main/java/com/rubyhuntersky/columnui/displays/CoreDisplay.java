package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/28/16.
 */

public interface CoreDisplay<T> {
    @NonNull
    Patch addPatch(Frame frame, Shape shape);

    @NonNull
    TextSize measureText(String text, TextStyle textStyle);
}
