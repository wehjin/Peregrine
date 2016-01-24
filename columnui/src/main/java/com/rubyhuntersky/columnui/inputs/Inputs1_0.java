package com.rubyhuntersky.columnui.inputs;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public class Inputs1_0<T> {
    Inputs1_1 bind(T value) {
        return new Inputs1_1<>(value);
    }
}
