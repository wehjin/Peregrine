package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.conditions.Column;

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

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape, Coloret color) {
        return original.addPatch(frame, shape, color);
    }

    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }
}