package com.rubyhuntersky.columnui.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.R;
import com.rubyhuntersky.columnui.Human;
import com.rubyhuntersky.columnui.displays.FixedDisplay;
import com.rubyhuntersky.columnui.presentations.MultiDisplayPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class UiView<T extends FixedDisplay<T>> extends ShapeDisplayView implements FixedDisplay<T> {

    public final String TAG = getClass().getSimpleName();
    private Human human;
    private T display;
    private MultiDisplayPresentation<T> multiDisplayPresentation = new MultiDisplayPresentation<>();
    private Ui<T> ui;
    private Ui<T> measuredUi;
    private Map<Integer, Integer> variableDimensions = new HashMap<>();

    public UiView(Context context) {
        super(context);
        initUiView();
    }

    public UiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUiView();
    }

    public UiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUiView();
    }

    private void initUiView() {
        human = new Human(getResources().getDimensionPixelSize(R.dimen.fingerTip),
                          getResources().getDimensionPixelSize(R.dimen.readingText));
        Log.d(TAG, "Human: " + human);
        setContentDescription(TAG);
    }

    public void clearVariableDimensions() {
        variableDimensions.clear();
    }

    public Presentation present(Ui<T> ui, Observer observer) {
        multiDisplayPresentation.cancel();
        this.ui = ui;
        final MultiDisplayPresentation<T> presentation1 = ui == null
              ? new MultiDisplayPresentation<T>()
              : new MultiDisplayPresentation<T>(ui, human, display,
                                                observer) {
                  @Override
                  protected void onCancel() {
                      super.onCancel();
                      UiView.this.ui = null;
                      requestLayout();
                  }
              };
        multiDisplayPresentation = presentation1;
        requestLayout();
        return presentation1;
    }

    abstract protected void setMeasuredDimensionFromDisplayDimensions(float fixed, float variable);
    abstract protected float getVariableDimension(Presentation presentation);
    abstract protected float getFixedDimension(int width, int height);
    abstract protected int getFixedDimensionMeasureSpec(int widthMeasureSpec, int heightMeasureSpec);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int fixedDimensionMeasureSpec = getFixedDimensionMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        final int fixedDimensionMode = MeasureSpec.getMode(fixedDimensionMeasureSpec);
        if (fixedDimensionMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("Fixed-dimension must be specified");
        }

        int fixedDimension = MeasureSpec.getSize(fixedDimensionMeasureSpec);
        if (ui == null) {
            setMeasuredDimensionFromDisplayDimensions(fixedDimension, 0);
            return;
        }

        if (ui != measuredUi) {
            variableDimensions.clear();
            measuredUi = ui;
        }
        if (!variableDimensions.containsKey(fixedDimension)) {
            Log.d(TAG, "onMeasure widthSpec: " + MeasureSpec.toString(widthMeasureSpec) + ", heightSpec: " + MeasureSpec
                  .toString(heightMeasureSpec));
            final T display = asDisplayWithFixedDimension(fixedDimension).withDelay().asType();
            final Presentation presentation = ui.present(human, display, Observer.EMPTY);
            final float variableDimension = getVariableDimension(presentation);
            presentation.cancel();
            variableDimensions.put(fixedDimension, (int) variableDimension);
        }
        final int variableDimension = variableDimensions.get(fixedDimension);
        Log.d(TAG, "onMeasure setMeasureDimension fixed: " + fixedDimension + ", variable: " + variableDimension);
        setMeasuredDimensionFromDisplayDimensions(fixedDimension, variableDimension);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        Log.d(TAG, "onSizeChanged");

        final float fixedDimension = getFixedDimension(w, h);
        if (fixedDimension == getFixedDimension(oldWidth, oldHeight)) {
            Log.d(TAG, "no change in fixed dimension");
            return;
        }
        display = asDisplayWithFixedDimension(fixedDimension);
        // Patch views sometimes don't show up if beginPresentation is called directly.
        post(new Runnable() {
            @Override
            public void run() {
                multiDisplayPresentation.setDisplay(display);
            }
        });
    }
}
