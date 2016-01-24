package com.rubyhuntersky.columnui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface OnBind<T, U> {
    U onBind(T condition);
}
