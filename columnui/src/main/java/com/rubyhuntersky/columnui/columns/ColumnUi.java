package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public abstract class ColumnUi implements Ui<Column> {

    public abstract Presentation present(Human human, Column column, Observer observer);

    public ColumnUi padHorizontal(final Sizelet padlet) {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final float padding = padlet.toFloat(human, column.fixedWidth);
                Column newColumn = column.withFixedWidth(column.fixedWidth - 2 * padding).withShift(padding, 0);
                presenter.addPresentation(ColumnUi.this.present(human, newColumn, presenter));
            }
        });
    }

    public ColumnUi padTop(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getDisplay();
                final ShiftColumn newColumn = column.withShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                newColumn.setShift(0, padding);
                final float newHeight = height + padding;
                presenter.addPresentation(new ResizePresentation(column.fixedWidth, newHeight, presentation));
            }
        });
    }

    public ColumnUi padBottom(final Sizelet padlet) {
        return expandBottom(Creator.gapColumn(padlet));
    }

    public ColumnUi padVertical(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getDisplay();
                final ShiftColumn newColumn = column.withShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                newColumn.setShift(0, padding);
                final float newHeight = height + 2 * padding;
                presenter.addPresentation(new ResizePresentation(column.fixedWidth, newHeight, presentation));
            }
        });
    }

    public ColumnUi expandVertical(final Sizelet heightlet) {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getDisplay();
                final float expansion = heightlet.toFloat(human, column.relatedHeight);
                final Column shiftColumn = column.withShift(0, expansion);
                final Presentation present = ColumnUi.this.present(human, shiftColumn, presenter);
                final float expanded = present.getHeight() + 2 * expansion;
                final Presentation resize = new ResizePresentation(column.fixedWidth, expanded, present);
                presenter.addPresentation(resize);
            }
        });
    }

    public ColumnUi placeBefore(@NonNull final ColumnUi background, final int gap) {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();
                final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
                final Presentation frontPresentation = ColumnUi.this.present(human, delayColumn, presenter);
                final Column backgroundColumn = column.withRelatedHeight(frontPresentation.getHeight());
                final Presentation backgroundPresentation = background.present(human, backgroundColumn, presenter);
                delayColumn.endDelay();
                final Pair<Presentation, Presentation> presentations =
                      new Pair<>(frontPresentation, backgroundPresentation);
                presenter.addPresentation(presentations.first);
                presenter.addPresentation(presentations.second);
            }
        });
    }

    public ColumnUi expandBottom(@NonNull final ColumnUi expansion) {
        final ColumnUi ui = this;
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                Human human = presenter.getHuman();
                Column column = presenter.getDisplay();

                final DelayColumn delayColumn = column.withDelay();
                final Presentation topPresentation = ui.present(human, delayColumn, presenter);
                final float topHeight = topPresentation.getHeight();
                final Column bottomColumn = column.withRelatedHeight(topHeight).withShift(0, topHeight);
                final Presentation bottomPresentation = expansion.present(human, bottomColumn, presenter);
                final Presentation bottomResize =
                      new ResizePresentation(bottomPresentation.getWidth(), topHeight + bottomPresentation
                            .getHeight(), bottomPresentation);
                delayColumn.endDelay();
                presenter.addPresentation(bottomResize);
                presenter.addPresentation(topPresentation);
            }
        });
    }

    public ColumnUi expandBottom(@NonNull final TileUi expansion) {
        return expandBottom(expansion.toColumn());
    }

    public <C> ColumnUi1<C> expandBottom(final ColumnUi1<C> expansion) {
        final ColumnUi columnUi = this;
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return columnUi.expandBottom(expansion.bind(condition));
            }
        });
    }

    public ColumnUi isolate() {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final Presentation presentation = ColumnUi.this.present(presenter.getHuman(),
                                                                        presenter.getDisplay(),
                                                                        new Observer() {
                                                                            @Override
                                                                            public void onReaction(Reaction reaction) {
                                                                                // Do nothing.
                                                                            }

                                                                            @Override
                                                                            public void onEnd() {
                                                                                // Do nothing.
                                                                            }

                                                                            @Override
                                                                            public void onError(Throwable throwable) {
                                                                                presenter.onError(throwable);
                                                                            }
                                                                        });
                presenter.addPresentation(presentation);
            }
        });
    }

    public static ColumnUi create(final OnPresent<Column> onPresent) {
        return new ColumnUi() {
            @Override
            public Presentation present(Human human, final Column column, Observer observer) {
                final BasePresenter<Column> presenter = new BasePresenter<Column>(human, column, observer) {
                    @Override
                    public float getWidth() {
                        return display.fixedWidth;
                    }

                    @Override
                    public float getHeight() {
                        float union = 0;
                        for (Presentation presentation : presentations) {
                            union = Math.max(union, presentation.getHeight());
                        }
                        return union;
                    }
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }
}
