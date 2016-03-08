package com.rubyhuntersky.gx.presenters;

import com.rubyhuntersky.gx.Observer;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.presentations.Presentation;

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