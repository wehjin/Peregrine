package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/24/16.
 */

public class BarWrapper extends Bar {
    private final Bar original;

    public BarWrapper(float height, float relatedWidth, int elevation, @NonNull Bar original) {
        super(height, relatedWidth, elevation);
        this.original = original;
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
