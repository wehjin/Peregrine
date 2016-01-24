package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Sizelet1 {
    public static Sizelet1 ZERO = new Sizelet1();
    public static Sizelet1 FULL = new Sizelet1(0, 1);

    private final float value;
    private final float portion;

    public Sizelet1(float value, float portion) {
        this.value = value;
        this.portion = portion;
    }

    public Sizelet1() {
        this(0, 0);
    }

    public float toFloat(Human human, float full) {
        return value + portion * full;
    }
}
