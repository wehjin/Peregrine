package com.rubyhuntersky.columnui.tiles;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public interface Bindable<T, U> {
    U bind(T condition);
}
