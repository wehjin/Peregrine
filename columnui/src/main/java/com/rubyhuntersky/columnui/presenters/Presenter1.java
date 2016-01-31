package com.rubyhuntersky.columnui.presenters;

import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.Presentation1;

import java.util.List;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presenter1<T, C> extends Presenter<T>, Presentation1<C> {

    Human getHuman();
    T getDisplay();
    List<Presentation> getPresentations();
    void addPresentation(Presentation presentation);
}