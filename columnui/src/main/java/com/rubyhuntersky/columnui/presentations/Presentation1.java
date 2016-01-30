package com.rubyhuntersky.columnui.presentations;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public interface Presentation1<T> extends Presentation {
    void rebind(T condition);
}
