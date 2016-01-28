package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.Display;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Column implements Display<Column> {

    public final float fixedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Column(float fixedWidth, float relatedHeight, int elevation) {
        this.fixedWidth = fixedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }

    public Column withFixedWidth(float fixedWidth) {
        return new WrapperColumn(fixedWidth, relatedHeight, elevation, this);
    }

    public Column withFrameShift(float horizontal, float vertical) {
        final FrameShiftColumn frameShiftColumn = withFrameShift();
        frameShiftColumn.setShift(horizontal, vertical);
        return frameShiftColumn;
    }

    public FrameShiftColumn withFrameShift() {
        return new FrameShiftColumn(this);
    }

    @NonNull
    @Override
    public DelayColumn withDelay() {
        return new DelayColumn(this);
    }

    @NonNull
    public Column withElevation(int elevation) {
        return elevation == this.elevation ? this : new WrapperColumn(fixedWidth, relatedHeight, elevation, this);
    }

    public Column withRelatedHeight(float relatedHeight) {
        return relatedHeight == this.relatedHeight
               ? null
               : new WrapperColumn(fixedWidth, relatedHeight, elevation, this);
    }

    @NonNull
    @Override
    public Column withFixedDimension(float fixedDimension) {
        return withFixedWidth(fixedDimension);
    }

    @NonNull
    @Override
    public Column asType() {
        return this;
    }
}
