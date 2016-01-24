package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui4<T1, T2, T3, T4> {
    abstract public Ui3<T2, T3, T4> bind1(final T1 condition);
    abstract public Ui3<T1, T3, T4> bind2(final T2 condition);
    abstract public Ui3<T1, T2, T4> bind3(final T3 condition);
    abstract public Ui3<T1, T2, T3> bind4(final T4 condition);

    static <T1, T2, T3, T4> Ui4<T1, T2, T3, T4> create(final OnBind<T1, Ui3<T2, T3, T4>> onBind1,
          final OnBind<T2, Ui3<T1, T3, T4>> onBind2, final OnBind<T3, Ui3<T1, T2, T4>> onBind3,
          final OnBind<T4, Ui3<T1, T2, T3>> onBind4) {
        return new Ui4<T1, T2, T3, T4>() {
            @Override
            public Ui3<T2, T3, T4> bind1(T1 condition) {
                return onBind1.onBind(condition);
            }

            @Override
            public Ui3<T1, T3, T4> bind2(T2 condition) {
                return onBind2.onBind(condition);
            }

            @Override
            public Ui3<T1, T2, T4> bind3(T3 condition) {
                return onBind3.onBind(condition);
            }

            @Override
            public Ui3<T1, T2, T3> bind4(T4 condition) {
                return onBind4.onBind(condition);
            }
        };
    }
}
