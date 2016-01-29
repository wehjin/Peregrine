package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.CoreDisplay;
import com.rubyhuntersky.columnui.displays.DelayDisplay;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class Tile implements CoreDisplay<Tile> {

    public final float relatedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Tile(float relatedWidth, float relatedHeight, int elevation) {
        this.relatedWidth = relatedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }

    @NonNull
    @Override
    public FrameShiftTile withFrameShift() {
        return new FrameShiftTile(this);
    }

    @NonNull
    @Override
    public DelayDisplay<Tile> withDelay() {
        return null;
    }

    @NonNull
    @Override
    public Tile withElevation(int elevation) {
        return elevation == this.elevation ? this : new WrapperTile(relatedWidth, relatedHeight, elevation, this);
    }

    @NonNull
    @Override
    public Tile asType() {
        return this;
    }
}
