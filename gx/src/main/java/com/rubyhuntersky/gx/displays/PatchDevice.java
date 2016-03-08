package com.rubyhuntersky.gx.displays;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.patches.Patch;
import com.rubyhuntersky.gx.Shape;
import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.basics.ShapeSize;
import com.rubyhuntersky.gx.basics.TextSize;
import com.rubyhuntersky.gx.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/29/16.
 */

public interface PatchDevice {
    @NonNull
    Patch addPatch(Frame frame, Shape shape);

    @NonNull
    TextSize measureText(String text, TextStyle textStyle);

    @NonNull
    ShapeSize measureShape(Shape shape);
}
