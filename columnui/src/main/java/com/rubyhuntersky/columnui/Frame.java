package com.rubyhuntersky.columnui;

import android.graphics.PointF;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Frame {
    public static final PointF POINT_ZERO = new PointF();
    public final PointF origin;
    public final PointF size;

    public Frame(PointF size) {
        this.origin = POINT_ZERO;
        this.size = size;
    }

    public Frame(float width, float height) {
        this.origin = POINT_ZERO;
        this.size = new PointF(width, height);
    }

    public Frame(float left, float top, float width, float height) {
        this.origin = new PointF(left, top);
        this.size = new PointF(width, height);
    }
}
