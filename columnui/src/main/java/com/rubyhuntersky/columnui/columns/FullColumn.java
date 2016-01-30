package com.rubyhuntersky.columnui.columns;

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

abstract public class FullColumn extends Column {
    public FullColumn(float fixedWidth, float relatedHeight, int elevation) {
        super(fixedWidth, relatedHeight, elevation, null);
    }

    @NonNull
    @Override
    abstract public Patch addPatch(Frame frame, Shape shape);

    @NonNull
    @Override
    abstract public TextSize measureText(String text, TextStyle textStyle);

    @NonNull
    @Override
    abstract public ShapeSize measureShape(Shape shape);
}
