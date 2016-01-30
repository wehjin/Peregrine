package com.rubyhuntersky.columnui.ui;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public interface Ui1<T, C> extends Ui<T> {

    C getStartCondition();
    Ui<T> bind(C condition);
}
