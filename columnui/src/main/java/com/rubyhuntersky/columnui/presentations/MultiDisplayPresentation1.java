package com.rubyhuntersky.columnui.presentations;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.ui.BoundUi1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class MultiDisplayPresentation1<T, C> extends BooleanPresentation implements Presentation1<C> {

    private final BoundUi1<T, C> ui;
    private final Human human;
    private final Observer observer;
    private Presentation1<C> presentation = new EmptyPresentation1<>();

    public MultiDisplayPresentation1(BoundUi1<T, C> ui, Human human, T display, Observer observer) {
        this.ui = ui;
        this.human = human;
        this.observer = observer;
        setDisplay(display);
    }

    public MultiDisplayPresentation1() {
        this.ui = null;
        this.human = null;
        this.observer = null;
    }

    public void setDisplay(T display) {
        if (isCancelled()) {
            return;
        }
        presentation.cancel();
        presentation = display == null ? new EmptyPresentation1<C>() : ui.present(human, display, observer);
    }

    @Override
    public void rebind(C condition) {
        presentation.rebind(condition);
    }

    @Override
    public float getWidth() {
        return isCancelled() ? 0 : presentation.getWidth();
    }

    @Override
    public float getHeight() {
        return isCancelled() ? 0 : presentation.getHeight();
    }

    @Override
    protected void onCancel() {
        presentation.cancel();
    }
}
