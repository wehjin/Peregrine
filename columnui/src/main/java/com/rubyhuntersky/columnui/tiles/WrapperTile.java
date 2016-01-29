package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.displays.CoreDisplay;

/**
 * @author wehjin
 * @since 1/29/16.
 */

public class WrapperTile extends Tile {

    protected final CoreDisplay<?> coreDisplay;

    public WrapperTile(float relatedWidth, float relatedHeight, int elevation, CoreDisplay<?> coreDisplay) {
        super(relatedWidth, relatedHeight, elevation);
        this.coreDisplay = coreDisplay;
    }

    protected WrapperTile(Tile original) {
        this(original.relatedWidth, original.relatedHeight, original.elevation, original);
    }


    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        return coreDisplay.addPatch(frame, shape);
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return coreDisplay.measureText(text, textStyle);
    }
}
