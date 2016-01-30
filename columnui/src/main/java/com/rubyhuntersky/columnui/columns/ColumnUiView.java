package com.rubyhuntersky.columnui.columns;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.UiView;
import com.rubyhuntersky.columnui.displays.DelayDisplay;
import com.rubyhuntersky.columnui.displays.FrameShiftDisplay;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.Presentation1;
import com.rubyhuntersky.columnui.tiles.Cui1;

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

    public <T> Presentation1<T> present(final Cui1<T> cui1, final T startCondition, final Observer observer) {
        return new Presentation1<T>() {

            boolean isCancelled;
            Presentation presentation = present(cui1.bind(startCondition), observer);

            @Override
            public void rebind(T condition) {
                if (isCancelled) {
                    throw new IllegalStateException("Rebind after cancelled");
                }
                presentation.cancel();
                presentation = present(cui1.bind(condition), observer);
            }

            @Override
            public float getWidth() {
                return isCancelled ? 0 : presentation.getWidth();
            }

            @Override
            public float getHeight() {
                return isCancelled ? 0 : presentation.getHeight();
            }

            @Override
            public boolean isCancelled() {
                return isCancelled;
            }

            @Override
            public void cancel() {
                if (isCancelled) {
                    return;
                }
                isCancelled = true;
                presentation.cancel();
            }
        };
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
