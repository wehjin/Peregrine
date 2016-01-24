package com.rubyhuntersky.columnui.conditions;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Condition;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class StringCondition extends Condition {
    @NonNull
    public final String string;

    public StringCondition(@NonNull String string) {
        this.string = string;
    }
}
