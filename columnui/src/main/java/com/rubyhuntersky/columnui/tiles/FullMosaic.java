package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.patches.Patch;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class FullMosaic extends Mosaic {
    public FullMosaic(float relatedWidth, float relatedHeight, int elevation) {
        super(relatedWidth, relatedHeight, elevation, null);
    }

    public FullMosaic() {
        this(0, 0, 0);
    }

    @NonNull
    @Override
    public abstract Patch addPatch(Frame frame, Shape shape);

    @NonNull
    @Override
    public abstract TextSize measureText(String text, TextStyle textStyle);

    @NonNull
    @Override
    public abstract ShapeSize measureShape(Shape shape);
}
