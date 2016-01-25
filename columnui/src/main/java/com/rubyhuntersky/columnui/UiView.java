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

import java.util.HashMap;

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

    public void setUi(Ui ui) {
        clearUi();
        this.ui = ui;
        if (ui != null) {
            beginPresentation();
        }
    }

    public void clearUi() {
        if (this.ui != null) {
            cancelPresentation();
            this.ui = null;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w == oldw) {
            return;
        }

        cancelPresentation();
        column = new Column(Range.of(0, w), Range.ZERO, 0) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret coloret) {
                if (shape == Shape.RECTANGLE) {
                    final View view = new View(getContext());
                    view.setBackgroundColor(coloret.toArgb());
                    setElevation(view, frame);
                    addView(view, getLayoutParams(frame, 0));
                    return new ViewPatch(view);
                } else if (shape instanceof TextShape) {
                    final TextShape textShape = (TextShape) shape;
                    final TextView textView = new TextView(getContext());
                    textView.setGravity(Gravity.TOP);
                    textView.setTextColor(coloret.toArgb());
                    textView.setTypeface(textShape.textStyle.typeface);
                    textView.setTextSize(textShape.textStyle.typeheight);
                    textView.setText(textShape.textString);
                    textView.setIncludeFontPadding(false);
                    final TextHeight textHeight = textShape.textSize.textHeight;
                    Frame newFrame = frame.withVerticalShift(-textHeight.topPadding)
                                          .withVerticalLength(textHeight.topPadding + textHeight.height);
                    setElevation(textView, newFrame);
                    addView(textView, getLayoutParams(newFrame, textHeight.height / 2));
                    return new ViewPatch(textView);
                } else {
                    return Patch.EMPTY;
                }
            }

            private void setElevation(View view, Frame frame) {
                ViewCompat.setElevation(view, levelPixels * frame.elevation);
            }

            @Override
            public TextSize measureText(String text, TextStyle textStyle) {
                Log.d(TAG, "meaureText: " + textStyle);
                return new TextSize(getTextWidth(text, textStyle),
                      getTextHeight(textStyle.typeface, textStyle.typeheight));
            }

            @NonNull
            private LayoutParams getLayoutParams(Frame frame, float additionalHeight) {
                final LayoutParams layoutParams = new LayoutParams((int) frame.horizontal.toLength(),
                      (int) (frame.vertical.toLength() + additionalHeight));
                layoutParams.leftMargin = (int) frame.horizontal.start;
                layoutParams.topMargin = (int) frame.vertical.start;
                return layoutParams;
            }
        };
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
        final float height = Math.max(0, bottomRow - topRow);
        final TextHeight textHeight = new TextHeight(height, topPadding);
        textHeightCache.put(typePair, textHeight);
        return textHeight;
    }

    private void cancelPresentation() {
        if (presentation != null) {
            presentation.cancel();
        }
    }

    private void init() {
        human = new Human(getResources().getDimensionPixelSize(R.dimen.fingerTip),
              getResources().getDimensionPixelSize(R.dimen.readingText));
        levelPixels = getResources().getDimensionPixelSize(R.dimen.elevationGap);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
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
}
