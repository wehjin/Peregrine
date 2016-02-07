package com.rubyhuntersky.columnui.basics;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Sizelet {
    public static final Sizelet FINGER = new Sizelet(0, 1, Ruler.FINGERTIP);
    public static final Sizelet TWO_THIRDS_FINGER = new Sizelet(0, .6666f, Ruler.FINGERTIP);
    public static final Sizelet HALF_FINGER = new Sizelet(0, .5f, Ruler.FINGERTIP);
    public static final Sizelet THIRD_FINGER = new Sizelet(0, .3333f, Ruler.FINGERTIP);
    public static final Sizelet QUARTER_FINGER = new Sizelet(0, .25f, Ruler.FINGERTIP);
    public static final Sizelet READABLE = new Sizelet(0, 1f, Ruler.READABLE);
    public static final Sizelet TITLE = new Sizelet(0, 1.8f, Ruler.READABLE);
    public static final Sizelet IMPORTANT = new Sizelet(0, 1.4f, Ruler.READABLE);
    public static final Sizelet PREVIOUS = new Sizelet(0, 1, Ruler.PREVIOUS);

    public static Sizelet ZERO = new Sizelet(0, 0, Ruler.ZERO);
    public static Sizelet FULL = new Sizelet(0, 1, Ruler.PREVIOUS);

    public static Sizelet ofValue(float value) {
        return new Sizelet(value, 0, Ruler.ZERO);
    }

    public static Sizelet ofPortion(float portion, Ruler ruler) {
        return new Sizelet(0, portion, ruler);
    }

    public static Sizelet pixels(int pixels) {
        return ofPortion(pixels, Ruler.PIXEL);
    }

    private final float value;
    private final float portion;

    private final Ruler ruler;

    public Sizelet(float value, float portion, Ruler ruler) {
        this.value = value;
        this.portion = portion;
        this.ruler = ruler;
    }

    public float toFloat(Human human, float containerSize) {
        float fullSize;
        if (ruler == Ruler.FINGERTIP) {
            fullSize = human.fingerPixels;
        } else if (ruler == Ruler.READABLE) {
            fullSize = human.textPixels;
        } else if (ruler == Ruler.PREVIOUS) {
            fullSize = containerSize;
        } else if (ruler == Ruler.PIXEL) {
            fullSize = 1;
        } else {
            fullSize = 0;
        }
        return value + portion * fullSize;
    }

    public enum Ruler {
        ZERO,
        FINGERTIP,
        READABLE,
        PREVIOUS,
        PIXEL
    }
}
