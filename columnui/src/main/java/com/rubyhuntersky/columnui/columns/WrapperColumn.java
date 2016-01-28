package com.rubyhuntersky.columnui.columns;

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

public class WrapperColumn extends Column {
    protected final Column original;

    public WrapperColumn(float fixedWidth, float relatedHeight, int elevation, @NonNull Column original) {
        super(fixedWidth, relatedHeight, elevation);
        this.original = original;
    }

    protected WrapperColumn(@NonNull Column original) {
        super(original.fixedWidth, original.relatedHeight, original.elevation);
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
