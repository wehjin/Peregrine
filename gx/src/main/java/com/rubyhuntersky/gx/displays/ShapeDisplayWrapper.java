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
public class ShapeDisplayWrapper implements PatchDevice {
    final protected PatchDevice basis;

    public ShapeDisplayWrapper(PatchDevice basis) {
        this.basis = basis;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        return basis.addPatch(frame, shape);
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return basis.measureText(text, textStyle);
    }

    @NonNull
    @Override
    public ShapeSize measureShape(Shape shape) {
        return basis.measureShape(shape);
    }
}
