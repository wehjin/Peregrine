package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Sizelet {
    public static final Sizelet FINGER = new Sizelet(0, 1, Ruler.FINGERTIP);
    public static final Sizelet HALF_FINGER = new Sizelet(0, .5f, Ruler.FINGERTIP);
    public static final Sizelet THIRD_FINGER = new Sizelet(0, .3333f, Ruler.FINGERTIP);
    public static final Sizelet QUARTER_FINGER = new Sizelet(0, .25f, Ruler.FINGERTIP);

    public static Sizelet ZERO = new Sizelet(0, 0, Ruler.ZERO);
    public static Sizelet FULL = new Sizelet(0, 1, Ruler.CONTAINER);

    public static Sizelet ofValue(float value) {
        return new Sizelet(value, 0, Ruler.ZERO);
    }

    public static Sizelet ofPortion(float portion, Ruler ruler) {
        return new Sizelet(0, portion, ruler);
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
        } else if (ruler == Ruler.READING_FONT) {
            fullSize = human.textPixels;
        } else if (ruler == Ruler.CONTAINER) {
            fullSize = containerSize;
        } else {
            fullSize = 0;
        }
        return value + portion * fullSize;
    }

    public enum Ruler {
        ZERO,
        FINGERTIP,
        READING_FONT,
        CONTAINER
    }
}
