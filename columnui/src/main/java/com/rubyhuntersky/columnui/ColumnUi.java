package com.rubyhuntersky.columnui;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Range;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.DelayColumn;
import com.rubyhuntersky.columnui.conditions.DelayedVerticalShiftColumn;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.VerticalRangePresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wehjin
 * @since 1/23/16.
 */

abstract public class ColumnUi {

    abstract public Presentation present(Human human, Column column, Observer observer);

    public ColumnUi padHorizontal(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Column column = presenter.getColumn();
                final float padding = padlet.toFloat(presenter.getHuman(), column.getWidth());
                Range newRange = column.horizontalRange.inset(padding);
                Column newColumn = column.withHorizontalRange(newRange);
                presenter.addPresentation(ui.present(presenter.getHuman(), newColumn, presenter));
            }
        });
    }

    public ColumnUi padTop(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final DelayedVerticalShiftColumn newColumn = column.withDelayedVerticalShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                newColumn.setVerticalShift(padding);
                final float newHeight = height + padding;
                presenter.addPresentation(new VerticalRangePresentation(column.getWidth(), newHeight, presentation));
            }
        });
    }

    public ColumnUi padBottom(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final Presentation presentation = ui.present(human, column, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                final float newHeight = height + padding;
                presenter.addPresentation(new VerticalRangePresentation(column.getWidth(), newHeight, presentation));
            }
        });
    }

    public ColumnUi padVertical(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final DelayedVerticalShiftColumn newColumn = column.withDelayedVerticalShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                newColumn.setVerticalShift(padding);
                final float newHeight = height + 2 * padding;
                presenter.addPresentation(new VerticalRangePresentation(column.getWidth(), newHeight, presentation));
            }
        });
    }

    public ColumnUi placeBefore(@NonNull final ColumnUi background, final int gap) {
        final ColumnUi ui = this;
        return create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
                final Presentation frontPresentation = ui.present(human, delayColumn, presenter);
                final Column backgroundColumn = column.withVerticalRange(Range.of(0, frontPresentation.getHeight()));
                final Presentation backgroundPresentation = background.present(human, backgroundColumn, presenter);
                delayColumn.endDelay();
                presenter.addPresentation(frontPresentation);
                presenter.addPresentation(backgroundPresentation);
            }
        });
    }

    public ColumnUi placeAbove(@NonNull final ColumnUi bottomUi) {
        final ColumnUi ui = this;
        return ColumnUi.create(new OnPresent() {
            @Override
            public void onPresent(Presenter presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getColumn();
                final DelayColumn delayColumn = column.withDelay();
                final Presentation topPresentation = ui.present(human, delayColumn, presenter);
                final float topHeight = topPresentation.getHeight();
                final Column bottomColumn = column.withVerticalRange(Range.of(0, topHeight));
                presenter.addPresentation(bottomUi.padTop(new Sizelet(0, topHeight, Sizelet.Ruler.PIXEL))
                                                  .present(human, bottomColumn, presenter));
                presenter.addPresentation(topPresentation);
                delayColumn.endDelay();
            }
        });
    }

    public static ColumnUi create(final OnPresent onPresent) {
        return new ColumnUi() {
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
                    public float getWidth() {
                        return column.getWidth();
                    }

                    @Override
                    public float getHeight() {
                        float height = 0;
                        for (Presentation presentation : presentations) {
                            height = Math.max(height, presentation.getHeight());
                        }
                        return height;
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
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }

}
