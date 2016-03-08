package com.rubyhuntersky.gx.internal.devices;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.basics.Frame;
import com.rubyhuntersky.gx.basics.ShapeSize;
import com.rubyhuntersky.gx.basics.TextSize;
import com.rubyhuntersky.gx.basics.TextStyle;
import com.rubyhuntersky.gx.internal.devices.patches.Patch;
import com.rubyhuntersky.gx.internal.shapes.Shape;

/**
 * @author wehjin
 * @since 1/29/16.
 */
public class PatchDeviceWrapper implements PatchDevice {
    final protected PatchDevice basis;

    public PatchDeviceWrapper(PatchDevice basis) {
        this.basis = basis;
    }

    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        return basis.addPatch(frame, shape);
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return basis.measureText(text, textStyle);
    }

    @NonNull
    @Override
    public ShapeSize measureShape(Shape shape) {
        return basis.measureShape(shape);
    }
}
