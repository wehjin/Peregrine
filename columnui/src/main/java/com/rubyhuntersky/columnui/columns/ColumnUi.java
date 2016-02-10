package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.operations.ExpandBottomOperation;
import com.rubyhuntersky.columnui.operations.ExpandVerticalOperation;
import com.rubyhuntersky.columnui.operations.PadHorizontalOperation;
import com.rubyhuntersky.columnui.operations.PlaceBeforeOperation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.columnui.tiles.TileUi1;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public abstract class ColumnUi implements Ui<Column> {

    public abstract Presentation present(Human human, Column column, Observer observer);

    public ColumnUi padHorizontal(final Sizelet padlet) {
        return new PadHorizontalOperation(padlet).applyTo(this);
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
        return new ExpandVerticalOperation(heightlet).applyTo(this);
    }

    public ColumnUi placeBefore(@NonNull final ColumnUi background, final int gap) {
        return new PlaceBeforeOperation(background, gap).applyTo(this);
    }

    public ColumnUi expandBottom(@NonNull final ColumnUi expansion) {
        return new ExpandBottomOperation(expansion).applyTo(this);
    }

    public ColumnUi expandBottom(@NonNull final TileUi expansion) {
        return expandBottom(expansion.toColumn());
    }

    public <C> ColumnUi1<C> expandBottom(final ColumnUi1<C> expansion) {
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return ColumnUi.this.expandBottom(expansion.bind(condition));
            }
        });
    }

    public <C1, C2> ColumnUi2<C1, C2> expandBottom(final ColumnUi2<C1, C2> expansion) {
        return ColumnUi2.create(new ColumnUi2.OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C2> onBind(C1 condition) {
                return ColumnUi.this.expandBottom(expansion.bind(condition));
            }
        });
    }

    public <C> ColumnUi1<C> expandBottom(TileUi1<C> expansion) {
        return expandBottom(expansion.toColumn());
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
