package com.rubyhuntersky.columnui.conditions;

import android.content.Context;

import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.R;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Human extends Condition {
    final public float fingerPixels;
    final public float textPixels;

    public Human(float fingerPixels, float textPixels) {
        this.fingerPixels = fingerPixels;
        this.textPixels = textPixels;
    }

    public Human(Context context) {
        this(context.getResources().getDimensionPixelSize(R.dimen.fingerTip),
              context.getResources().getDimensionPixelSize(R.dimen.readingText));
    }

    @Override
    public String toString() {
        return "Human{" +
              "fingerPixels=" + fingerPixels +
              ", textPixels=" + textPixels +
              '}';
    }
}
