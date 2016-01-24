package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Sizelet2 {

    public static final Sizelet2 ZERO = new Sizelet2(Sizelet1.ZERO, Sizelet1.ZERO);
    private final Sizelet1 origin;
    private final Sizelet1 size;

    public Sizelet2(Sizelet1 origin, Sizelet1 size) {
        this.origin = origin;
        this.size = size;
    }
}
