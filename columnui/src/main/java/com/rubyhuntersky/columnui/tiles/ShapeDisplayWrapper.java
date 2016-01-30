package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.displays.ShapeDisplay;

/**
 * @author wehjin
 * @since 1/29/16.
 */
public class ShapeDisplayWrapper implements ShapeDisplay {
    final protected ShapeDisplay basis;

    public ShapeDisplayWrapper(ShapeDisplay basis) {
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
