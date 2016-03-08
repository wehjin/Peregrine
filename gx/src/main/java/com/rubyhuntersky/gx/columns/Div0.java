package com.rubyhuntersky.gx.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.gx.Creator;
import com.rubyhuntersky.gx.Observer;
import com.rubyhuntersky.gx.Reaction;
import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.columns.operations.ExpandDownDivOperation1;
import com.rubyhuntersky.gx.columns.operations.ExpandVerticalDivOperation0;
import com.rubyhuntersky.gx.columns.operations.PadHorizontalDivOperation0;
import com.rubyhuntersky.gx.columns.operations.PlaceBeforeDivOperation0;
import com.rubyhuntersky.gx.presentations.Presentation;
import com.rubyhuntersky.gx.presentations.ResizePresentation;
import com.rubyhuntersky.gx.presenters.BasePresenter;
import com.rubyhuntersky.gx.presenters.OnPresent;
import com.rubyhuntersky.gx.presenters.Presenter;
import com.rubyhuntersky.gx.ui.Ui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public abstract class Div0 implements Ui<Column> {

    static final public Div0 EMPTY = new Div0() {
        @Override
        public Presentation present(Human human, Column column, Observer observer) {
            return Presentation.EMPTY;
        }
    };

    private Div0() {
    }

    public static Div0 create(final OnPresent<Column> onPresent) {
        return new Div0() {
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

    public abstract Presentation present(Human human, Column column, Observer observer);

    public Div0 padHorizontal(final Sizelet padlet) {
        return new PadHorizontalDivOperation0(padlet).apply(this);
    }

    public Div0 padTop(final Sizelet padlet) {
        final Div0 ui = this;
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

    public Div0 padBottom(final Sizelet padlet) {
        return expandDown(Creator.gapColumn(padlet));
    }

    public Div0 padVertical(final Sizelet padlet) {
        final Div0 ui = this;
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

    public Div0 expandVertical(final Sizelet heightlet) {
        return new ExpandVerticalDivOperation0(heightlet).apply(this);
    }

    public Div0 placeBefore(@NonNull final Div0 background, final int gap) {
        return new PlaceBeforeDivOperation0(background, gap).apply(this);
    }

    public Div1<Div0> expandDown() {
        return new ExpandDownDivOperation1().applyFuture0(this);
    }

    public Div0 expandDown(@NonNull final Div0 expansion) {
        return new ExpandDownDivOperation1().apply0(this, expansion);
    }

    public <C> Div1<C> expandDown(final Div1<C> expansion) {
        return new ExpandDownDivOperation1().apply1(this, expansion);
    }

    public <C1, C2> Div2<C1, C2> expandDown(final Div2<C1, C2> expansion) {
        return new ExpandDownDivOperation1().apply2(this, expansion);
    }

    public <C1, C2, C3> Div3<C1, C2, C3> expandDown(final Div3<C1, C2, C3> expansion) {
        return new ExpandDownDivOperation1().apply3(this, expansion);
    }

    public Div0 isolate() {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(final Presenter<Column> presenter) {
                final Presentation presentation = Div0.this.present(presenter.getHuman(),
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
}
