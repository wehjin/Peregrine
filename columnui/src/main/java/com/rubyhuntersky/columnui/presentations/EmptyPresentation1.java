package com.rubyhuntersky.columnui.presentations;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class EmptyPresentation1<T> extends EmptyPresentation implements Presentation1<T> {
    @Override
    public void rebind(T condition) {
        // Do nothing
    }
}
