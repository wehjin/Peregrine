package com.rubyhuntersky.gx.internal;

import com.rubyhuntersky.gx.client.Human;
import com.rubyhuntersky.gx.client.Observer;
import com.rubyhuntersky.gx.client.Presentation;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public interface Ui<T> {
    Presentation present(Human human, T device, Observer observer);
}