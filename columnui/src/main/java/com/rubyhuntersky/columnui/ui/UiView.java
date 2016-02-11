package com.rubyhuntersky.columnui.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.R;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.TextHeight;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.displays.FixedDisplay;
import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.presentations.MultiDisplayPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;
import com.rubyhuntersky.columnui.shapes.ViewShape;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class UiView<T extends FixedDisplay<T>> extends FrameLayout implements FixedDisplay<T> {

    public final String TAG = getClass().getSimpleName();
    private Human human;
    private T display;
    private MultiDisplayPresentation<T> multiDisplayPresentation = new MultiDisplayPresentation<>();
    private Ui<T> ui;
    public int elevationPixels;
    private Ui<T> measuredUi;
    private Map<Integer, Integer> variableDimensions = new HashMap<>();
    private TextRuler textRuler;
    private ShapeRuler shapeRuler;

    public UiView(Context context) {
        super(context);
        init(context);
    }

    public UiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        textRuler = new TextRuler(context);
        shapeRuler = new ShapeRuler(context);
        human = new Human(getResources().getDimensionPixelSize(R.dimen.fingerTip),
                          getResources().getDimensionPixelSize(R.dimen.readingText));
        Log.d(TAG, "Human: " + human);
        elevationPixels = getResources().getDimensionPixelSize(R.dimen.elevationGap);
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


    @NonNull
    @Override
    public Patch addPatch(Frame frame, Shape shape) {
        if (shape instanceof RectangleShape) {
            return getRectanglePatch(frame, (RectangleShape) shape);
        } else if (shape instanceof TextShape) {
            return getTextPatch(frame, (TextShape) shape);
        } else if (shape instanceof ViewShape) {
            final ViewShape viewShape = (ViewShape) shape;
            return getViewPatch(viewShape.createView(getContext()), frame, 0);
        } else {
            return Patch.EMPTY;
        }
    }

    @NonNull
    @Override
    public TextSize measureText(String text, TextStyle textStyle) {
        return textRuler.measure(text, textStyle);
    }

    @NonNull
    @Override
    public ShapeSize measureShape(Shape shape) {
        return shapeRuler.measure(shape);
    }

    @NonNull
    private Patch getTextPatch(Frame frame, TextShape textShape) {
        final TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.TOP);
        textView.setTextColor(textShape.textStyle.coloret.toArgb());
        textView.setTypeface(textShape.textStyle.typeface);
        textView.setTextSize(textShape.textStyle.typeheight);
        textView.setText(textShape.textString);
        textView.setIncludeFontPadding(false);
        final TextHeight textHeight = textShape.textSize.textHeight;
        Frame newFrame = frame.withVerticalShift(-textHeight.topPadding)
              .withVerticalLength(textHeight.topPadding + textHeight.height);
        return getViewPatch(textView, newFrame, textHeight.height / 2);
    }

    @NonNull
    private Patch getRectanglePatch(Frame frame, RectangleShape rectangleShape) {
        final View view = new View(getContext());
        final int color = rectangleShape.coloret.toArgb();
        view.setBackgroundColor(color);
        view.setContentDescription(String.format("Rectangle{%x}", color));
        return getViewPatch(view, frame, 0);
    }

    @NonNull
    private Patch getViewPatch(final View view, final Frame frame, float additionalHeight) {
        setElevation(view, frame);
        Log.d(TAG, "Add view: " + view + " frame: " + frame);
        addView(view, getPatchLayoutParams(frame, additionalHeight));
        return new Patch() {
            @Override
            public void remove() {
                Log.d(TAG, "Remove view: " + view + " frame: " + frame);
                UiView.this.removeView(view);
            }
        };
    }

    private void setElevation(View view, Frame frame) {
        ViewCompat.setElevation(view, elevationPixels * frame.elevation);
    }

    @NonNull
    private LayoutParams getPatchLayoutParams(Frame frame, float additionalHeight) {
        final LayoutParams layoutParams = new FrameLayout.LayoutParams((int) frame.horizontal.toLength(),
                                                                       (int) (frame.vertical.toLength() + additionalHeight));
        layoutParams.leftMargin = (int) frame.horizontal.start;
        layoutParams.topMargin = (int) frame.vertical.start;
        return layoutParams;
    }
}
