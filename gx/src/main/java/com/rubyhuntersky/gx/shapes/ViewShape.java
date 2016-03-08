package com.rubyhuntersky.gx.shapes;

import android.content.Context;
import android.view.View;

import com.rubyhuntersky.gx.Shape;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class ViewShape extends Shape {
    public abstract View createView(Context context);
}
