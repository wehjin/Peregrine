package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.Human;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class Ui {

    abstract public Presentation present(Human human, Column column, Observer observer);

    static Ui create(final OnPresent onPresent) {
        return new Ui() {
            @Override
            public Presentation present(final Human human, final Column column, final Observer observer) {
                final Presenter presenter = new Presenter() {

                    boolean isCancelled;

                    List<Presentation> presentations = new ArrayList<>();

                    public Human getHuman() {
                        return human;
                    }

                    public Column getColumn() {
                        // TODO override column and remove patches when cancelled.
                        return column;
                    }

                    public void addPresentation(Presentation presentation) {
                        if (isCancelled) {
                            presentation.cancel();
                        } else {
                            presentations.add(presentation);
                        }
                    }

                    @Override
                    public Range getVerticalRange() {
                        Range range = new Range(Float.MAX_VALUE, Float.MIN_VALUE);
                        for (Presentation presentation : presentations) {
                            range = range.union(presentation.getVerticalRange());
                        }
                        return range;
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
                        observer.onReaction(reaction);
                    }

                    @Override
                    public void onEnd() {
                        cancel();
                        observer.onEnd();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        cancel();
                        observer.onError(throwable);
                    }
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }
}
