package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;

import java.util.List;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presenter<T> extends Observer, Presentation {

    Human getHuman();
    T getDisplay();
    List<Presentation> getPresentations();
    void addPresentation(Presentation presentation);
}