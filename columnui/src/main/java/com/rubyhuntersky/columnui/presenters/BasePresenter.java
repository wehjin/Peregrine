package com.rubyhuntersky.columnui.presenters;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wehjin
 * @since 1/27/16.
 */
public abstract class BasePresenter<T> implements Presenter<T> {

    protected final Human human;
    protected final T display;
    protected final Observer observer;
    protected final List<Presentation> presentations = new ArrayList<>();
    protected boolean isCancelled;

    public BasePresenter(Human human, T display, Observer observer) {
        this.human = human;
        this.display = display;
        this.observer = observer;
    }

    public Human getHuman() {
        return human;
    }

    public T getDisplay() {
        // TODO override display and remove patches when cancelled.
        return display;
    }

    @Override
    public List<Presentation> getPresentations() {
        return presentations;
    }

    public void addPresentation(Presentation presentation) {
        if (isCancelled) {
            presentation.cancel();
        } else {
            presentations.add(presentation);
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void cancel() {
        if (isCancelled) return;
        isCancelled = true;
        final List<Presentation> toCancel = new ArrayList<>(presentations);
        presentations.clear();
        Collections.reverse(toCancel);
        for (Presentation presentation : toCancel) {
            presentation.cancel();
        }
    }

    @Override
    public void onReaction(Reaction reaction) {
        if (isCancelled) return;
        observer.onReaction(reaction);
    }

    @Override
    public void onEnd() {
        if (isCancelled) return;
        cancel();
        observer.onEnd();
    }

    @Override
    public void onError(Throwable throwable) {
        if (isCancelled) return;
        cancel();
        observer.onError(throwable);
    }
}
