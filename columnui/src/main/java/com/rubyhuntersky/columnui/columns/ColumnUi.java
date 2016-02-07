package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;
import android.util.Pair;

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
import com.rubyhuntersky.columnui.ui.PresentationMaker;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public abstract class ColumnUi implements Ui<Column> {

    public abstract Presentation present(Human human, Column column, Observer observer);

    public ColumnUi padHorizontal(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                presenter.addPresentation(presentWithHorizontalPadding(padlet,
                                                                       presenter.getHuman(),
                                                                       presenter.getDisplay(),
                                                                       presenter,
                                                                       new PresentationMaker<Presentation, Column>() {

                                                                           @Override
                                                                           public Presentation present(Human human, Column display, Observer observer, int index) {
                                                                               return ui.present(human,
                                                                                                 display,
                                                                                                 observer);
                                                                           }

                                                                           @Override
                                                                           public Presentation resize(float width, float height, Presentation basis) {
                                                                               return null;
                                                                           }

                                                                       }));
            }
        });
    }

    public static <P extends Presentation> P presentWithHorizontalPadding(Sizelet padlet, Human human, Column column, Observer observer, PresentationMaker<P, Column> maker) {
        final float padding = padlet.toFloat(human, column.fixedWidth);
        Column newColumn = column.withFixedWidth(column.fixedWidth - 2 * padding).withShift(padding, 0);
        return maker.present(human, newColumn, observer, 0);
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
        final ColumnUi ui = this;
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                presenter.addPresentation(presentWithBottomPadding(padlet,
                                                                   presenter.getHuman(),
                                                                   presenter.getDisplay(),
                                                                   presenter,
                                                                   new PresentationMaker<Presentation, Column>() {
                                                                       @Override
                                                                       public Presentation present(Human human, Column display, Observer observer, int index) {
                                                                           return ui.present(human, display, observer);
                                                                       }

                                                                       @Override
                                                                       public Presentation resize(float width, float height, Presentation basis) {
                                                                           return new ResizePresentation(width,
                                                                                                         height,
                                                                                                         basis);
                                                                       }

                                                                   }));
            }
        });
    }

    public static <P extends Presentation> P presentWithBottomPadding(Sizelet padlet1, Human human, Column column, Observer observer, PresentationMaker<P, Column> maker) {
        final P presentation = maker.present(human, column, observer, 0);
        final float height = presentation.getHeight();
        final float padding = padlet1.toFloat(human, height);
        final float newHeight = height + padding;
        return maker.resize(column.fixedWidth, newHeight, presentation);
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
                final Pair<Presentation, Presentation> presentations =
                      presentFirstBeforeSecond(gap, presenter.getHuman(), presenter
                            .getDisplay(), presenter, new PresentationMaker<Presentation, Column>() {
                          @Override
                          public Presentation present(Human human, Column display, Observer observer, int index) {
                              if (index == 0) {
                                  return ColumnUi.this.present(human, display, observer);
                              } else if (index == 1) {
                                  return background.present(human, display, observer);
                              }
                              return null;
                          }

                          @Override
                          public Presentation resize(float width, float height, Presentation basis) {
                              return null;
                          }

                      });
                presenter.addPresentation(presentations.first);
                presenter.addPresentation(presentations.second);
            }
        });
    }

    @NonNull
    public static <P extends Presentation> Pair<P, P> presentFirstBeforeSecond(int gap, Human human, Column column, Observer observer, PresentationMaker<P, Column> maker) {
        final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
        final P frontPresentation = maker.present(human, delayColumn, observer, 0);
        final Column backgroundColumn = column.withRelatedHeight(frontPresentation.getHeight());
        final P backgroundPresentation = maker.present(human, backgroundColumn, observer, 1);
        delayColumn.endDelay();
        return new Pair<>(frontPresentation, backgroundPresentation);
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
