package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Sizelet1 {
    public static Sizelet1 ZERO = new Sizelet1();

    private final float value;

    public Sizelet1() {
        value = 0f;
    }

    public float toFloat(Human human) {
        return value;
    }
}
