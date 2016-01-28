package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Bar extends Condition {

    final public float height;
    final public float relatedWidth;
    final public int elevation;

    public Bar(float height, float relatedWidth, int elevation) {
        this.height = height;
        this.relatedWidth = relatedWidth;
        this.elevation = elevation;
    }

    @NonNull
    abstract public Patch addPatch(Frame frame, Shape shape);

    abstract public TextSize measureText(String text, TextStyle textStyle);


    public Bar withElevation(int elevation) {
        return this.elevation == elevation ? this : new BarWrapper(height, relatedWidth, elevation, this);
    }

    public Bar withHeight(float height) {
        return this.height == height ? this : new BarWrapper(height, relatedWidth, elevation, this);
    }

    public Bar withRelatedWidth(float relatedWidth) {
        return this.relatedWidth == relatedWidth ? this : new BarWrapper(height, relatedWidth, elevation, this);
    }
}
