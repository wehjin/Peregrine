package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui1<T> {
    abstract public Ui bind(final T condition);

    static <T> Ui1<T> create(final OnBind<T, Ui> onBind) {
        return new Ui1<T>() {
            @Override
            public Ui bind(final T condition) {
                return onBind.onBind(condition);
            }
        };
    }
}
