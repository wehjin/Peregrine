package com.rubyhuntersky.columnui.inputs;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Inputs2_0<T1, T2> {

    Inputs2_1<T1, T2> bind1(T1 value) {
        return new Inputs2_1<>(value);
    }
}
