package com.rubyhuntersky.columnui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.Human;

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

        cancelPresentation();
        column = new Column(Range.of(0, w), 0) {
            @NonNull
            @Override
            public Patch addPatch(Frame frame, Shape shape, Coloret coloret) {
                final View view = new View(getContext());
                view.setBackgroundColor(coloret.toArgb());
                final int elevation = levelPixels * frame.elevation;
                Log.d(TAG, "Elevation: " + elevation);
                ViewCompat.setElevation(view, elevation);

                addView(view, getLayoutParams(frame));
                return new Patch() {
                    @Override
                    public void remove() {
                        removeView(view);
                    }
                };
            }

            @NonNull
            private LayoutParams getLayoutParams(Frame frame) {
                final LayoutParams layoutParams = new LayoutParams((int) frame.horizontal.toLength(),
                      (int) frame.vertical.toLength());
                layoutParams.leftMargin = (int) frame.horizontal.start;
                layoutParams.topMargin = (int) frame.vertical.start;
                return layoutParams;
            }
        };
        beginPresentation();
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
    }
}
