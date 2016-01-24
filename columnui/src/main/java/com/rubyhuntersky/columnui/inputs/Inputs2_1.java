package com.rubyhuntersky.columnui.inputs;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Inputs2_1<T1, T2> {

    final public T1 value1;

    public Inputs2_1(T1 value) {
        this.value1 = value;
    }

    Inputs2_2<T1, T2> bind2(T2 value) {
        return new Inputs2_2<>(this.value1, value);
    }
}
