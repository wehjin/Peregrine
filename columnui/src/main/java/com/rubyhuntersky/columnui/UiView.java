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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextHeight;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.displays.FixedDisplay;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;
import com.rubyhuntersky.columnui.shapes.ViewShape;

import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class UiView<T extends FixedDisplay<T>> extends FrameLayout implements FixedDisplay<T> {

    public final String TAG = getClass().getSimpleName();
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
        setContentDescription(TAG);
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
            Log.d(TAG, "Begin presentation");
            presentation = ui.present(human, display, new Observer() {
                @Override
                public void onReaction(Reaction reaction) {
                    Log.d(TAG, "onReaction: " + reaction);
                }

                @Override
                public void onEnd() {
                    Log.d(TAG, "onEnd");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "onError", throwable);
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
            presentation.cancel();
            variableDimensions.put(fixedDimension, (int) variableDimension);
        }
        final int variableDimension = variableDimensions.get(fixedDimension);
        Log.d(TAG, "onMeasure setMeasureDimension fixed: " + fixedDimension + ", variable: " + variableDimension);
        setMeasuredDimensionFromDisplayDimensions(fixedDimension, variableDimension);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");

        final float fixedDimension = getFixedDimension(w, h);
        if (fixedDimension == getFixedDimension(oldw, oldh)) {
            Log.d(TAG, "no change in fixed dimension");
            return;
        }

        cancelPresentation();
        display = withFixedDimension(fixedDimension);
        // Patch views sometimes don't show up if beginPresentation is called directly.
        post(new Runnable() {
            @Override
            public void run() {
                beginPresentation();
            }
        });
    }

    public float getTextWidth(String textString, TextStyle textStyle) {
        TextView ruler = getTextRuler();
        ruler.setTypeface(textStyle.typeface);
        ruler.setTextSize(textStyle.typeheight);
        ruler.setText(textString);
        ruler.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return ruler.getMeasuredWidth();
    }

    @NonNull
    public TextHeight getTextHeight(Typeface typeface, int typeheight) {
        final Pair<Typeface, Integer> typePair = new Pair<>(typeface, typeheight);
        if (textHeightCache.containsKey(typePair)) {
            return textHeightCache.get(typePair);
        }

        TextView textView = getTextRuler();
        textView.setText("E");
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

    private TextView getTextRuler() {
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            textView.setMinHeight(0);
            textView.setGravity(Gravity.TOP);
            textView.setBackgroundColor(Color.BLACK);
            textView.setTextColor(Color.BLUE);
            textView.setIncludeFontPadding(false);
        }
        return textView;
    }

    private void cancelPresentation() {
        if (presentation != null) {
            Log.d(TAG, "Cancel presentation");
            presentation.cancel();
            presentation = null;
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
        Log.d(TAG, "measureText: " + textStyle);
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
