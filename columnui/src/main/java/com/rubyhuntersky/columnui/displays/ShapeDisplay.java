package com.rubyhuntersky.columnui.displays;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/29/16.
 */

public interface ShapeDisplay {
    @NonNull
    Patch addPatch(Frame frame, Shape shape);

    @NonNull
    TextSize measureText(String text, TextStyle textStyle);

    @NonNull
    ShapeSize measureShape(Shape shape);
}
