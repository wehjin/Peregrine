package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.CoreDisplay;
import com.rubyhuntersky.columnui.displays.DelayDisplay;
import com.rubyhuntersky.columnui.displays.ShapeDisplay;
import com.rubyhuntersky.columnui.displays.ShapeDisplayWrapper;

/**
 * @author wehjin
 * @since 1/28/16.
 */

public class Tile extends ShapeDisplayWrapper implements CoreDisplay<Tile> {

    public final float relatedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Tile(float relatedWidth, float relatedHeight, int elevation, ShapeDisplay shapeDisplay) {
        super(shapeDisplay);
        this.relatedWidth = relatedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }

    protected Tile(Tile basis) {
        this(basis.relatedWidth, basis.relatedHeight, basis.elevation, basis);
    }

    @NonNull
    @Override
    public ShiftTile withShift() {
        return new ShiftTile(this);
    }

    @NonNull
    @Override
    public DelayDisplay<Tile> withDelay() {
        return null;
    }

    @NonNull
    @Override
    public Tile withElevation(int elevation) {
        return elevation == this.elevation ? this : new Tile(relatedWidth, relatedHeight, elevation, this);
    }

    @NonNull
    @Override
    public Tile asType() {
        return this;
    }

}
