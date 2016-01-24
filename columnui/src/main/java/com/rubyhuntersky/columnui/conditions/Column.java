package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.Condition;
import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.Widthlet;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Column extends Condition {

    @NonNull
    final public Widthlet widthlet;

    public Column(@NonNull Widthlet widthlet) {
        this.widthlet = widthlet;
    }

    @NonNull
    public Patch addPatch(Frame frame, Shape shape, Coloret color) {
        return Patch.EMPTY;
    }
}
