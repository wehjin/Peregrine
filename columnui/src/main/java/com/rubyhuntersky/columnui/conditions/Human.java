package com.rubyhuntersky.columnui.conditions;

import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.Sizelet1;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Human extends Condition {
    final public Sizelet1 fingerPixels;
    final public Sizelet1 textPixels;

    public Human(Sizelet1 fingerPixels, Sizelet1 textPixels) {
        this.fingerPixels = fingerPixels;
        this.textPixels = textPixels;
    }
}
