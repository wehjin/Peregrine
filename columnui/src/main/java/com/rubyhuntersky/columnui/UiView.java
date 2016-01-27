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

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.shapes.RectangleShape;
import com.rubyhuntersky.columnui.shapes.TextShape;
import com.rubyhuntersky.columnui.shapes.ViewShape;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class UiView extends FrameLayout {

    public static final String TAG = UiView.class.getSimpleName();
    private Human human;
    private Column column;
    private Presentation presentation;
    private Ui ui;
    private int levelPixels;
    private Paint textPaint;
    private TextView textView;
    private final HashMap<Pair<Typeface, Integer>, TextHeight> textHeightCache = new HashMap<>();
    private MyColumn myColumn;
    private Ui measuredUi;
    private int measuredUiHeight;
    private Map<Integer, Integer> measuredHeights = new HashMap<>();

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
        levelPixels = getResources().getDimensionPixelSize(R.dimen.elevationGap);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        myColumn = new MyColumn(300);
    }

    public void setUi(Ui ui) {
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
        if (ui != null && column != null) {
            presentation = ui.present(human, column, new Observer() {
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("Width must be specified");
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (ui == null) {
            setMeasuredDimension(width, 0);
            return;
        }

        if (ui != measuredUi) {
            measuredHeights.clear();
            measuredUi = ui;
        }
        if (!measuredHeights.containsKey(width)) {
            Log.d(TAG, "onMeasure widthSpec: " + MeasureSpec.toString(widthMeasureSpec) + ", heightSpec: " + MeasureSpec
                  .toString(heightMeasureSpec));
            final Column column = myColumn.withHorizontalRange(Range.of(0, width)).withDelay();
            final Presentation presentation = ui.present(human, column, Observer.EMPTY);
            final float presentationHeight = presentation.getVerticalRange().toLength();
            presentation.cancel();
            measuredHeights.put(width, (int) presentationHeight);
            Log.d(TAG, "onMeasure height: " + (int) presentationHeight);
        }
        measuredUiHeight = measuredHeights.get(width);
        setMeasuredDimension(width, measuredUiHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w == oldw) {
            return;
        }

        cancelPresentation();
        column = myColumn.withHorizontalRange(Range.of(0, w));
        beginPresentation();
    }

    private float getTextWidth(String text, TextStyle textStyle) {
        textPaint.setTypeface(textStyle.typeface);
        textPaint.setTextSize(textStyle.typeheight);
        return textPaint.measureText(text);
    }

    @NonNull
    private TextHeight getTextHeight(Typeface typeface, int typeheight) {
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

    private class ViewPatch implements Patch {
        private final View view;

        public ViewPatch(View view) {
            this.view = view;
        }

        @Override
        public void remove() {
            removeView(view);
        }
    }

    private class MyColumn extends Column {
        public MyColumn(int width) {
            super(Range.of(0, width), Range.ZERO, 0);
        }

        private void setElevation(View view, Frame frame) {
            ViewCompat.setElevation(view, levelPixels * frame.elevation);
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
                final View view = viewShape.createView(getContext());
                return getViewPatch(view, frame, 0);
            } else {
                return Patch.EMPTY;
            }
        }

        @Override
        public TextSize measureText(String text, TextStyle textStyle) {
            Log.d(TAG, "meaureText: " + textStyle);
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
            addView(view, getLayoutParams(frame, additionalHeight));
            return new ViewPatch(view);
        }

        @NonNull
        private LayoutParams getLayoutParams(Frame frame, float additionalHeight) {
            final LayoutParams layoutParams = new LayoutParams((int) frame.horizontal.toLength(),
                  (int) (frame.vertical.toLength() + additionalHeight));
            layoutParams.leftMargin = (int) frame.horizontal.start;
            layoutParams.topMargin = (int) frame.vertical.start;
            return layoutParams;
        }
    }
}
