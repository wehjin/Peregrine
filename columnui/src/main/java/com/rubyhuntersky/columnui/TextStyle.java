package com.rubyhuntersky.columnui;

import android.graphics.Typeface;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class TextStyle {
    public final int typeheight;
    public final Typeface typeface;
    public final Coloret coloret;

    public TextStyle(float typeheight, Typeface typeface, Coloret coloret) {
        this.typeheight = (int) typeheight;
        this.typeface = typeface;
        this.coloret = coloret;
    }

    @Override
    public String toString() {
        return "TextStyle{" +
              "typeheight=" + typeheight +
              ", typeface=" + typeface +
              ", coloret=" + coloret +
              '}';
    }
}
