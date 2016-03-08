package com.rubyhuntersky.gx.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.displays.FixedDisplay;
import com.rubyhuntersky.gx.displays.PatchDevice;
import com.rubyhuntersky.gx.displays.ShapeDisplayWrapper;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Bar extends ShapeDisplayWrapper implements FixedDisplay<Bar> {

    public final float fixedHeight;
    public final float relatedWidth;
    public final int elevation;

    public Bar(float fixedHeight, float relatedWidth, int elevation, PatchDevice patchDevice) {
        super(patchDevice);
        this.fixedHeight = fixedHeight;
        this.relatedWidth = relatedWidth;
        this.elevation = elevation;
    }

    public Bar(Bar basis) {
        this(basis.fixedHeight, basis.relatedWidth, basis.elevation, basis);
    }

    @NonNull
    @Override
    public DelayBar withDelay() {
        return new DelayBar(this);
    }

    @NonNull
    public ShiftBar withShift() {
        return new ShiftBar(this);
    }


    @NonNull
    @Override
    public Bar asDisplayWithFixedDimension(float fixedDimension) {
        return withFixedHeight(fixedDimension);
    }

    @NonNull
    public Bar withFixedHeight(float fixedHeight) {
        return fixedHeight == this.fixedHeight ? this : new Bar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    public Bar withRelatedWidth(float relatedWidth) {
        return relatedWidth == this.relatedWidth ? this : new Bar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public Bar withElevation(int elevation) {
        return elevation == this.elevation ? this : new Bar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public Bar asType() {
        return this;
    }
}
