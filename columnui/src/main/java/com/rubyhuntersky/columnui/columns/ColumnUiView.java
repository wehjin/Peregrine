package com.rubyhuntersky.columnui.columns;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.rubyhuntersky.columnui.ui.UiView;
import com.rubyhuntersky.columnui.displays.DelayDisplay;
import com.rubyhuntersky.columnui.displays.FrameShiftDisplay;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class ColumnUiView extends UiView<Column> {

    public static final String TAG = ColumnUiView.class.getSimpleName();
    private Column column;

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
        column = new Column(0, 0, 0, this);
    }

    @NonNull
    @Override
    public Column asType() {
        return column;
    }

    @NonNull
    @Override
    public Column asDisplayWithFixedDimension(float fixedDimension) {
        return column.asDisplayWithFixedDimension(fixedDimension);
    }

    @NonNull
    @Override
    public DelayDisplay<Column> withDelay() {
        return column.withDelay();
    }

    @NonNull
    @Override
    public FrameShiftDisplay<Column> withShift() {
        return column.withShift();
    }

    @NonNull
    @Override
    public Column withElevation(int elevation) {
        return column.withElevation(elevation);
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
