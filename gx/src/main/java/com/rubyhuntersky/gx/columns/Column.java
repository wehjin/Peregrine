package com.rubyhuntersky.gx.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.displays.FixedDisplay;
import com.rubyhuntersky.gx.displays.PatchDevice;
import com.rubyhuntersky.gx.displays.ShapeDisplayWrapper;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Column extends ShapeDisplayWrapper implements FixedDisplay<Column> {

    public final float fixedWidth;
    public final float relatedHeight;
    public final int elevation;

    public Column(float fixedWidth, float relatedHeight, int elevation, PatchDevice patchDevice) {
        super(patchDevice);
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

    public Column withShift(float horizontal, float vertical) {
        final ShiftColumn frameShiftColumn = withShift();
        frameShiftColumn.setShift(horizontal, vertical);
        return frameShiftColumn;
    }

    @NonNull
    public ShiftColumn withShift() {
        return new ShiftColumn(this);
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
    public Column asDisplayWithFixedDimension(float fixedDimension) {
        return withFixedWidth(fixedDimension);
    }

    @NonNull
    @Override
    public Column asType() {
        return this;
    }
}
