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

public class Mosaic extends ShapeDisplayWrapper implements CoreDisplay<Mosaic> {

    public final float relatedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Mosaic(float relatedWidth, float relatedHeight, int elevation, ShapeDisplay shapeDisplay) {
        super(shapeDisplay);
        this.relatedWidth = relatedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }

    protected Mosaic(Mosaic basis) {
        this(basis.relatedWidth, basis.relatedHeight, basis.elevation, basis);
    }

    @NonNull
    @Override
    public ShiftMosaic withShift() {
        return new ShiftMosaic(this);
    }

    @NonNull
    @Override
    public DelayDisplay<Mosaic> withDelay() {
        return null;
    }

    @NonNull
    @Override
    public Mosaic withElevation(int elevation) {
        return elevation == this.elevation ? this : new Mosaic(relatedWidth, relatedHeight, elevation, this);
    }

    @NonNull
    @Override
    public Mosaic asType() {
        return this;
    }

}