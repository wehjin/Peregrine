package com.rubyhuntersky.columnui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextHeight;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.displays.Display;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;
import com.rubyhuntersky.columnui.shapes.ViewShape;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class UiView<T extends Display<T>> extends FrameLayout implements Display<T> {

    public static final String TAG = UiView.class.getSimpleName();
    private Human human;
    private T display;
    private Presentation presentation;
    private BaseUi<T> ui;
    public int elevationPixels;
    private Paint textPaint;
    private TextView textView;
    private final HashMap<Pair<Typeface, Integer>, TextHeight> textHeightCache = new HashMap<>();
    private BaseUi<T> measuredUi;
    private Map<Integer, Integer> variableDimensions = new HashMap<>();

    public UiView(Context context) {
        super(context);
        init();
    }

    public UiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        human = new Human(getResources().getDimensionPixelSize(R.dimen.fingerTip),
              getResources().getDimensionPixelSize(R.dimen.readingText));
        Log.d(TAG, "Human: " + human);
        elevationPixels = getResources().getDimensionPixelSize(R.dimen.elevationGap);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public void setUi(BaseUi<T> ui) {
        clearUi();
        this.ui = ui;
        if (ui != null) {
            beginPresentation();
            requestLayout();
        }
    }

    public void clearUi() {
        if (this.ui != null) {
            cancelPresentation();
            this.ui = null;
            requestLayout();
        }
    }

    private void beginPresentation() {
        cancelPresentation();
        if (ui != null && display != null) {
            presentation = ui.present(human, display, new Observer() {
                @Override
                public void onReaction(Reaction reaction) {
                }

                @Override
                public void onEnd() {
                }

                @Override
                public void onError(Throwable throwable) {
                }
            });
        }
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
            final T display = withFixedDimension(fixedDimension).withDelay().asType();
            final Presentation presentation = ui.present(human, display, Observer.EMPTY);
            final float variableDimension = getVariableDimension(presentation);
            Log.d(TAG, "onMeasure variable-dimension: " + (int) variableDimension);
            presentation.cancel();
            variableDimensions.put(fixedDimension, (int) variableDimension);
        }
        setMeasuredDimensionFromDisplayDimensions(fixedDimension, variableDimensions.get(fixedDimension));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final float fixedDimension = getFixedDimension(w, h);
        if (fixedDimension == getFixedDimension(oldw, oldh)) {
            return;
        }

        cancelPresentation();
        display = withFixedDimension(fixedDimension);
        beginPresentation();
    }

    public float getTextWidth(String text, TextStyle textStyle) {
        textPaint.setTypeface(textStyle.typeface);
        textPaint.setTextSize(textStyle.typeheight);
        return textPaint.measureText(text);
    }

    @NonNull
    public TextHeight getTextHeight(Typeface typeface, int typeheight) {
        final Pair<Typeface, Integer> typePair = new Pair<>(typeface, typeheight);
        if (textHeightCache.containsKey(typePair)) {
            return textHeightCache.get(typePair);
        }

        if (textView == null) {
            textView = new TextView(getContext());
            textView.setMinHeight(0);
            textView.setGravity(Gravity.TOP);
            textView.setBackgroundColor(Color.BLACK);
            textView.setTextColor(Color.BLUE);
            textView.setText("E");
            textView.setIncludeFontPadding(false);
        }
        textView.setTypeface(typeface);
        textView.setTextSize(typeheight);
        textView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        final Bitmap bitmap = Bitmap.createBitmap(textView.getMeasuredWidth(), textView.getMeasuredHeight(),
              Bitmap.Config.ARGB_8888);
        Log.d(TAG, "Bitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());
        textView.layout(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        textView.draw(canvas);
        final int rowCount = bitmap.getHeight();
        final int columnCount = bitmap.getWidth();
        int topRow = rowCount;
        int bottomRow = 0;
        int col = columnCount / 2;
        for (int row = 0; row < rowCount; row++) {
            final int pixel = bitmap.getPixel(col, row);
            if ((pixel & 0xff) > 128) {
                topRow = Math.min(topRow, row);
                bottomRow = Math.max(bottomRow, row);
            }
        }
        Log.d(TAG, "E limits: " + topRow + ", " + bottomRow);
        final float topPadding = topRow;
        final float height = Math.max(0, bottomRow - topRow + 1);
        final TextHeight textHeight = new TextHeight(height, topPadding);
        textHeightCache.put(typePair, textHeight);
        return textHeight;
    }

    private void cancelPresentation() {
        if (presentation != null) {
            presentation.cancel();
        }
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
        Log.d(UiView.TAG, "measureText: " + textStyle);
        return new TextSize(getTextWidth(text, textStyle), getTextHeight(textStyle.typeface, textStyle.typeheight));
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
        view.setBackgroundColor(rectangleShape.coloret.toArgb());
        return getViewPatch(view, frame, 0);
    }

    @NonNull
    private Patch getViewPatch(View view, Frame frame, float additionalHeight) {
        setElevation(view, frame);
        addView(view, getPatchLayoutParams(frame, additionalHeight));
        return new ViewPatch(this, view);
    }

    private void setElevation(View view, Frame frame) {
        ViewCompat.setElevation(view, elevationPixels * frame.elevation);
    }

    @NonNull
    private FrameLayout.LayoutParams getPatchLayoutParams(Frame frame, float additionalHeight) {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) frame.horizontal.toLength(),
              (int) (frame.vertical.toLength() + additionalHeight));
        layoutParams.leftMargin = (int) frame.horizontal.start;
        layoutParams.topMargin = (int) frame.vertical.start;
        return layoutParams;
    }
}
