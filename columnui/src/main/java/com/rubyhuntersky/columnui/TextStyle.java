package com.rubyhuntersky.columnui;

import android.graphics.Typeface;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class TextStyle {
    public final Sizelet height;
    public final Typeface typeface;
    public final Coloret coloret;

    public TextStyle(Sizelet height, Typeface typeface, Coloret coloret) {
        this.height = height;
        this.typeface = typeface;
        this.coloret = coloret;
    }
}
