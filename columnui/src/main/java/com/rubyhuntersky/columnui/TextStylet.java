package com.rubyhuntersky.columnui;

import android.graphics.Typeface;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class TextStylet {
    public static final TextStylet DARK_TITLE = new TextStylet(Sizelet.TITLE, Typeface.DEFAULT, Coloret.DARK_TEXT);

    public final Sizelet heightlet;
    public final Typeface typeface;
    public final Coloret coloret;

    public TextStylet(Sizelet heightlet, Typeface typeface, Coloret coloret) {
        this.heightlet = heightlet;
        this.typeface = typeface;
        this.coloret = coloret;
    }

    public TextStyle toStyle(Human human, float contextSize) {
        return new TextStyle(heightlet.toFloat(human, contextSize), typeface, coloret);
    }
}
