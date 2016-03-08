package com.rubyhuntersky.gx.internal.presenters;

import com.rubyhuntersky.gx.client.Observer;
import com.rubyhuntersky.gx.client.Human;
import com.rubyhuntersky.gx.client.Presentation;

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