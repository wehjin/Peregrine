package com.rubyhuntersky.columnui.ui;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public interface Ui<T> {
    Presentation present(Human human, T display, Observer observer);
}
