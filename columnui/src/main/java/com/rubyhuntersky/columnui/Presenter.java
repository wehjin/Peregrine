package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presenter<T> extends Observer, Presentation {

    Human getHuman();
    T getDisplay();
    void addPresentation(Presentation presentation);
}