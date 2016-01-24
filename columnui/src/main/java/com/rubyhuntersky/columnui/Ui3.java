package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui3<T1, T2, T3> {

    abstract public Ui2<T2, T3> bind1(final T1 condition);
    abstract public Ui2<T1, T3> bind2(final T2 condition);
    abstract public Ui2<T1, T2> bind3(final T3 condition);

    static <T1, T2, T3> Ui3<T1, T2, T3> create(final OnBind<T1, Ui2<T2, T3>> onBind1,
          final OnBind<T2, Ui2<T1, T3>> onBind2, final OnBind<T3, Ui2<T1, T2>> onBind3) {
        return new Ui3<T1, T2, T3>() {
            @Override
            public Ui2<T2, T3> bind1(T1 condition) {
                return onBind1.onBind(condition);
            }

            @Override
            public Ui2<T1, T3> bind2(T2 condition) {
                return onBind2.onBind(condition);
            }

            @Override
            public Ui2<T1, T2> bind3(T3 condition) {
                return onBind3.onBind(condition);
            }
        };
    }
}
