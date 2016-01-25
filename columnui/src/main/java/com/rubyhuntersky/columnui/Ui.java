package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.DelayColumn;
import com.rubyhuntersky.columnui.conditions.DelayedVerticalShiftColumn;
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

    public Ui padHorizontal(final Sizelet padlet) {
        final Ui ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final float padding = padlet.toFloat(presenter.getHuman(), column.horizontalRange.toLength());
                Range newRange = column.horizontalRange.inset(padding);
                Column newColumn = column.withHorizontalRange(newRange);
                presenter.addPresentation(ui.present(presenter.getHuman(), newColumn, presenter));
            }
        });
    }

    public Ui padTop(final Sizelet padlet) {
        final Ui ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final DelayedVerticalShiftColumn newColumn = presenter.getColumn().withDelayedVerticalShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final Range verticalRange = presentation.getVerticalRange();
                final float padding = padlet.toFloat(human, verticalRange.toLength());
                newColumn.setVerticalShift(padding);
                final Range newVerticalRange = verticalRange.moveStart(-padding).shift(padding);
                presenter.addPresentation(new VerticalRangePresentation(newVerticalRange, presentation));
            }
        });
    }

    public Ui padBottom(final Sizelet padlet) {
        final Ui ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Presentation presentation = ui.present(human, presenter.getColumn(), presenter);
                final Range verticalRange = presentation.getVerticalRange();
                final float padding = padlet.toFloat(human, verticalRange.toLength());
                final Range newVerticalRange = verticalRange.moveEnd(padding);
                presenter.addPresentation(new VerticalRangePresentation(newVerticalRange, presentation));
            }
        });
    }

    public Ui padVertical(final Sizelet padlet) {
        final Ui ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final DelayedVerticalShiftColumn newColumn = presenter.getColumn().withDelayedVerticalShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final Range verticalRange = presentation.getVerticalRange();
                final float padding = padlet.toFloat(human, verticalRange.toLength());
                newColumn.setVerticalShift(padding);
                final Range newVerticalRange = verticalRange.outset(padding).shift(padding);
                presenter.addPresentation(new VerticalRangePresentation(newVerticalRange, presentation));
            }
        });
    }

    public Ui placeBefore(@NonNull final Ui background, final int gap) {
        final Ui ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
                final Presentation foregroundPresentation = ui.present(human, delayColumn, presenter);
                final Column backgroundColumn = column.withVerticalRange(foregroundPresentation.getVerticalRange());
                final Presentation backgroundPresentation = background.present(human, backgroundColumn, presenter);
                delayColumn.endDelay();
                presenter.addPresentation(foregroundPresentation);
                presenter.addPresentation(backgroundPresentation);
            }
        });
    }

    public Ui placeAbove(@NonNull Ui below) {
        return placeBefore(below, 0);
    }

    static Ui create(final OnPresent onPresent) {
        return new Ui() {
            @Override
            public Presentation present(final Human human, final Column column, final Observer observer) {
                final Presenter presenter = new Presenter() {

                    boolean isCancelled;

                    final List<Presentation> presentations = new ArrayList<>();

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
                        Range range = column.verticalRange;
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
