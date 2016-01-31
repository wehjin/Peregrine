package com.rubyhuntersky.columnui.presenters;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

import java.util.Collections;
import java.util.List;

/**
 * @author wehjin
 * @since 1/31/16.
 */
public class SwitchPresenter<T> implements Presenter<T> {

    private final Human human;
    private final T display;
    private final Observer observer;
    boolean isCancelled;
    Presentation presentation;

    public SwitchPresenter(Human human, T display, Observer observer) {
        this.human = human;
        this.display = display;
        this.observer = observer;
        presentation = Presentation.EMPTY;
    }

    @Override
    public float getWidth() {
        return presentation.getWidth();
    }

    @Override
    public float getHeight() {
        return presentation.getHeight();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void cancel() {
        if (isCancelled) return;
        isCancelled = true;
        presentation.cancel();
    }

    @Override
    public void onReaction(Reaction reaction) {
        observer.onReaction(reaction);
    }

    @Override
    public void onEnd() {
        observer.onEnd();
    }

    @Override
    public void onError(Throwable throwable) {
        observer.onError(throwable);
    }

    @Override
    public Human getHuman() {
        return human;
    }

    @Override
    public T getDisplay() {
        return display;
    }

    @Override
    public List<Presentation> getPresentations() {
        return Collections.singletonList(presentation);
    }

    public void setPresentation(Presentation presentation) {
        if (isCancelled) {
            presentation.cancel();
            return;
        }
        this.presentation.cancel();
        this.presentation = presentation;
    }

    @Override
    public void addPresentation(Presentation presentation) {
        setPresentation(presentation);
    }
}
