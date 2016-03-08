package com.rubyhuntersky.gx.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.displays.FixedDisplay;
import com.rubyhuntersky.gx.displays.PatchDevice;
import com.rubyhuntersky.gx.displays.PatchDeviceWrapper;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class BarExtender extends PatchDeviceWrapper implements FixedDisplay<BarExtender> {

    public final float fixedHeight;
    public final float relatedWidth;
    public final int elevation;

    public BarExtender(float fixedHeight, float relatedWidth, int elevation, PatchDevice patchDevice) {
        super(patchDevice);
        this.fixedHeight = fixedHeight;
        this.relatedWidth = relatedWidth;
        this.elevation = elevation;
    }

    public BarExtender(BarExtender basis) {
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
    public BarExtender asDisplayWithFixedDimension(float fixedDimension) {
        return withFixedHeight(fixedDimension);
    }

    @NonNull
    public BarExtender withFixedHeight(float fixedHeight) {
        return fixedHeight == this.fixedHeight ? this : new BarExtender(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    public BarExtender withRelatedWidth(float relatedWidth) {
        return relatedWidth == this.relatedWidth ? this : new BarExtender(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public BarExtender withElevation(int elevation) {
        return elevation == this.elevation ? this : new BarExtender(fixedHeight, relatedWidth, elevation, this);
    }

    @NonNull
    @Override
    public BarExtender asType() {
        return this;
    }
}