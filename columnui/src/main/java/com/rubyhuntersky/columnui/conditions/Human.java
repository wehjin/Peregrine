package com.rubyhuntersky.columnui.conditions;

import com.rubyhuntersky.columnui.Condition;

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
}
