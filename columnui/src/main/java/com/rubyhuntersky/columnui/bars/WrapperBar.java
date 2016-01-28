package com.rubyhuntersky.columnui.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/24/16.
 */

public class WrapperBar extends Bar {
    protected final Bar original;

    public WrapperBar(float fixedHeight, float relatedWidth, int elevation, @NonNull Bar original) {
        super(fixedHeight, relatedWidth, elevation);
        this.original = original;
    }

    protected WrapperBar(@NonNull Bar original) {
        super(original.fixedHeight, original.relatedWidth, original.elevation);
        this.original = original;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        return original.addPatch(frame, shape);
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return original.measureText(text, textStyle);
    }
}
