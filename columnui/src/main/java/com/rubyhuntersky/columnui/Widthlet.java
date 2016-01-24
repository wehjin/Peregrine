package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Widthlet extends Sizelet1 {

    public static final Widthlet ZERO = new Widthlet(0f, 0);
    public static final Widthlet TEN = new Widthlet(10f, 0);

    public Widthlet(float value, float portion) {
        super(value, portion);
    }

    public Widthlet() {
    }
}
