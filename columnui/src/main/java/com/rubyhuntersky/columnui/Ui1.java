package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.columns.ColumnUi;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui1<T> {
    abstract public ColumnUi bind(final T condition);

    static <T> Ui1<T> create(final OnBind<T, ColumnUi> onBind) {
        return new Ui1<T>() {
            @Override
            public ColumnUi bind(final T condition) {
                return onBind.onBind(condition);
            }
        };
    }
}
