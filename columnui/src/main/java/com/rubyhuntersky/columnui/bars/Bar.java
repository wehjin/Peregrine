package com.rubyhuntersky.columnui.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.FixedDisplay;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Bar implements FixedDisplay<Bar> {

    public final float fixedHeight;
    public final float relatedWidth;
    public final int elevation;

    public Bar(float fixedHeight, float relatedWidth, int elevation) {
        this.fixedHeight = fixedHeight;
        this.relatedWidth = relatedWidth;
        this.elevation = elevation;
    }

    @NonNull
    @Override
    public DelayBar withDelay() {
        return new DelayBar(this);
    }

    @NonNull
    public FrameShiftBar withFrameShift() {
        return new FrameShiftBar(this);
    }


    @NonNull
    @Override
    public Bar withFixedDimension(float fixedDimension) {
        return withFixedHeight(fixedDimension);
    }

    @NonNull
    public Bar withFixedHeight(float fixedHeight) {
        return fixedHeight == this.fixedHeight ? this : new WrapperBar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    public Bar withRelatedWidth(float relatedWidth) {
        return relatedWidth == this.relatedWidth ? this : new WrapperBar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public Bar withElevation(int elevation) {
        return elevation == this.elevation ? this : new WrapperBar(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public Bar asType() {
        return this;
    }
}
