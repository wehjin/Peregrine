package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.displays.FixedDisplay;
import com.rubyhuntersky.columnui.displays.ShapeDisplay;
import com.rubyhuntersky.columnui.tiles.ShapeDisplayWrapper;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Column extends ShapeDisplayWrapper implements FixedDisplay<Column> {

    public final float fixedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Column(float fixedWidth, float relatedHeight, int elevation, ShapeDisplay shapeDisplay) {
        super(shapeDisplay);
        this.fixedWidth = fixedWidth;
        this.relatedHeight = relatedHeight;
        this.elevation = elevation;
    }

    protected Column(@NonNull Column original) {
        this(original.fixedWidth, original.relatedHeight, original.elevation, original);
    }

    public Column withFixedWidth(float fixedWidth) {
        return new Column(fixedWidth, relatedHeight, elevation, this);
    }

    public Column withFrameShift(float horizontal, float vertical) {
        final FrameShiftColumn frameShiftColumn = withShift();
        frameShiftColumn.setShift(horizontal, vertical);
        return frameShiftColumn;
    }

    @NonNull
    public FrameShiftColumn withShift() {
        return new FrameShiftColumn(this);
    }

    @NonNull
    @Override
    public DelayColumn withDelay() {
        return new DelayColumn(this);
    }

    @NonNull
    public Column withElevation(int elevation) {
        return elevation == this.elevation ? this : new Column(fixedWidth, relatedHeight, elevation, this);
    }

    public Column withRelatedHeight(float relatedHeight) {
        return relatedHeight == this.relatedHeight ? null : new Column(fixedWidth, relatedHeight, elevation, this);
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
