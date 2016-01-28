package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/27/16.
 */

abstract public class BarUi {
    abstract public Presentation present(Human human, Column column, Observer observer);
}
