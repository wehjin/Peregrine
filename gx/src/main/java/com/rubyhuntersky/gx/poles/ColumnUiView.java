package com.rubyhuntersky.gx.poles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.rubyhuntersky.gx.internal.devices.DelayDisplay;
import com.rubyhuntersky.gx.internal.devices.ShiftDisplay;
import com.rubyhuntersky.gx.client.Presentation;
import com.rubyhuntersky.gx.internal.UiView;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class ColumnUiView extends UiView<Pole> {

    public static final String TAG = ColumnUiView.class.getSimpleName();
    private Pole pole;

    public ColumnUiView(Context context) {
        super(context);
        init();
    }

    public ColumnUiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColumnUiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pole = new Pole(0, 0, 0, this);
    }

    @NonNull
    @Override
    public Pole asType() {
        return pole;
    }

    @NonNull
    @Override
    public Pole asDisplayWithFixedDimension(float fixedDimension) {
        return pole.asDisplayWithFixedDimension(fixedDimension);
    }

    @NonNull
    @Override
    public DelayDisplay<Pole> withDelay() {
        return pole.withDelay();
    }

    @NonNull
    @Override
    public ShiftDisplay<Pole> withShift() {
        return pole.withShift();
    }

    @NonNull
    @Override
    public Pole withElevation(int elevation) {
        return pole.withElevation(elevation);
    }

    @Override
    protected int getFixedDimensionMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        return widthMeasureSpec;
    }

    @Override
    protected void setMeasuredDimensionFromDisplayDimensions(float fixed, float variable) {
        setMeasuredDimension((int) fixed, (int) variable);
    }

    @Override
    protected float getVariableDimension(Presentation presentation) {
        return presentation.getHeight();
    }

    @Override
    protected float getFixedDimension(int width, int height) {
        return width;
    }
}
