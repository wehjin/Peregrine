package com.rubyhuntersky.columnui.presenters;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

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