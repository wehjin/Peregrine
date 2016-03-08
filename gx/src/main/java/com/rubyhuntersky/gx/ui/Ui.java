package com.rubyhuntersky.gx.ui;

import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.Observer;
import com.rubyhuntersky.gx.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public interface Ui<T> {
    Presentation present(Human human, T display, Observer observer);
}