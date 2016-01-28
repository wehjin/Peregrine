package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.Range;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/24/16.
 */

public class ColumnWrapper extends Column {
    private final Column original;

    public ColumnWrapper(Range horizontalRange, Range verticalRange, int elevation, @NonNull Column original) {
        super(horizontalRange, verticalRange, elevation);
        this.original = original;
    }

    @Override
    public float getWidth() {
        return original.getWidth();
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        return original.addPatch(frame, shape);
    }

    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }
}
