package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui2<T1, T2> {
    abstract public Ui1<T2> bind1(final T1 condition);
    abstract public Ui1<T1> bind2(final T2 condition);

    static <T1, T2> Ui2<T1, T2> create(final OnBind<T1, Ui1<T2>> onBind1, final OnBind<T2, Ui1<T1>> onBind2) {
        return new Ui2<T1, T2>() {
            @Override
            public Ui1<T2> bind1(T1 condition) {
                return onBind1.onBind(condition);
            }

            @Override
            public Ui1<T1> bind2(T2 condition) {
                return onBind2.onBind(condition);
            }
        };
    }
}
