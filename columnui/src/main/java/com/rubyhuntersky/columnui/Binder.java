package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Binder<T, U> {
    T getCondition();
    U getBound();
}
