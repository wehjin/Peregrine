package com.rubyhuntersky.columnui.presentations;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class MultiDisplayPresentation<T> extends BooleanPresentation implements Presentation {

    private final Ui<T> ui;
    private final Human human;
    private final Observer observer;
    private Presentation presentation = new EmptyPresentation();

    public MultiDisplayPresentation(Ui<T> ui, Human human, T display, Observer observer) {
        this.ui = ui;
        this.human = human;
        this.observer = observer;
        setDisplay(display);
    }

    public MultiDisplayPresentation() {
        this.ui = null;
        this.human = null;
        this.observer = null;
    }

    public void setDisplay(T display) {
        if (isCancelled()) {
            return;
        }
        presentation.cancel();
        presentation = display == null ? new EmptyPresentation() : ui.present(human, display, observer);
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
