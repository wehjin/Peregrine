package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.NoRebindPresentation;
import com.rubyhuntersky.columnui.presentations.PairPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
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
                presenter.addPresentation(
                      presentWithHorizontalPadding(padlet, presenter.getHuman(), presenter.getDisplay(), presenter,
                            new PresentationMaker<Presentation, Column>() {

                                @Override
                                public Presentation present(Human human, Column display, Observer observer, int index) {
                                    return ui.present(human, display, observer);
                                }

                                @Override
                                public Presentation resize(float width, float height, Presentation basis) {
                                    return null;
                                }

                            }));
            }
        });
    }

    public static <P extends Presentation> P presentWithHorizontalPadding(Sizelet padlet, Human human, Column column,
          Observer observer, PresentationMaker<P, Column> maker) {
        final float padding = padlet.toFloat(human, column.fixedWidth);
        Column newColumn = column.withFixedWidth(column.fixedWidth - 2 * padding).withFrameShift(padding, 0);
        return maker.present(human, newColumn, observer, 0);
    }

    public ColumnUi padTop(final Sizelet padlet) {
        final ColumnUi ui = this;
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Human human = presenter.getHuman();
                final Column column = presenter.getDisplay();
                final FrameShiftColumn newColumn = column.withShift();
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
                presenter.addPresentation(
                      presentWithBottomPadding(padlet, presenter.getHuman(), presenter.getDisplay(), presenter,
                            new PresentationMaker<Presentation, Column>() {
                                @Override
                                public Presentation present(Human human, Column display, Observer observer, int index) {
                                    return ui.present(human, display, observer);
                                }

                                @Override
                                public Presentation resize(float width, float height, Presentation basis) {
                                    return new ResizePresentation(width, height, basis);
                                }

                            }));
            }
        });
    }

    public static <P extends Presentation> P presentWithBottomPadding(Sizelet padlet1, Human human, Column column,
          Observer observer, PresentationMaker<P, Column> maker) {
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
                final FrameShiftColumn newColumn = column.withShift();
                final Presentation presentation = ui.present(human, newColumn, presenter);
                final float height = presentation.getHeight();
                final float padding = padlet.toFloat(human, height);
                newColumn.setShift(0, padding);
                final float newHeight = height + 2 * padding;
                presenter.addPresentation(new ResizePresentation(column.fixedWidth, newHeight, presentation));
            }
        });
    }

    public ColumnUi placeBefore(@NonNull final ColumnUi background, final int gap) {
        return create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Pair<Presentation, Presentation> presentations = presentFirstBeforeSecond(gap,
                      presenter.getHuman(), presenter.getDisplay(), presenter,
                      new PresentationMaker<Presentation, Column>() {
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
    public static <P extends Presentation> Pair<P, P> presentFirstBeforeSecond(int gap, Human human, Column column,
          Observer observer, PresentationMaker<P, Column> maker) {
        final DelayColumn delayColumn = column.withElevation(column.elevation + gap).withDelay();
        final P frontPresentation = maker.present(human, delayColumn, observer, 0);
        final Column backgroundColumn = column.withRelatedHeight(frontPresentation.getHeight());
        final P backgroundPresentation = maker.present(human, backgroundColumn, observer, 1);
        delayColumn.endDelay();
        return new Pair<>(frontPresentation, backgroundPresentation);
    }

    public ColumnUi expandBottom(@NonNull final ColumnUi bottomUi) {
        final ColumnUi ui = this;
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                final Pair<Presentation, Presentation> pair = presentBottomExpansion(presenter.getHuman(),
                      presenter.getDisplay(), presenter, new PresentationMaker<Presentation, Column>() {
                          @Override
                          public Presentation present(Human human, Column display, Observer observer, int index) {
                              if (index == 0) {
                                  return ui.present(human, display, observer);
                              }
                              if (index == 1) {
                                  return bottomUi.present(human, display, observer);
                              }
                              return null;
                          }

                          @Override
                          public Presentation resize(float width, float height, Presentation basis) {
                              return new ResizePresentation(width, height, basis);
                          }
                      });
                presenter.addPresentation(pair.second);
                presenter.addPresentation(pair.first);
            }
        });
    }

    @NonNull
    public static <P extends Presentation> Pair<P, P> presentBottomExpansion(Human human, Column column,
          Observer observer, PresentationMaker<P, Column> maker) {
        final DelayColumn delayColumn = column.withDelay();
        final P topPresentation = maker.present(human, delayColumn, observer, 0);
        final float topHeight = topPresentation.getHeight();
        final Column bottomColumn = column.withRelatedHeight(topHeight).withFrameShift(0, topHeight);
        final P bottomPresentation = maker.present(human, bottomColumn, observer, 1);
        final P bottomResize = maker.resize(bottomPresentation.getWidth(), topHeight + bottomPresentation.getHeight(),
              bottomPresentation);
        delayColumn.endDelay();
        return new Pair<>(topPresentation, bottomResize);
    }

    public <C> ColumnUi1<C> expandBottom(final ColumnUi1<C> columnUi1) {
        final ColumnUi columnUi = this;
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1 onBind(final C condition) {
                return BoundCui1.create(new BoundCui1.OnPresent1() {

                    @Override
                    public Presentation onPresent(Human human, Column column, Observer observer) {
                        return new PairPresentation(presentBottomExpansion(human, column, observer,
                              new PresentationMaker<Presentation, Column>() {
                                  @Override
                                  public Presentation present(Human human, Column display, Observer observer,
                                        int index) {
                                      if (index == 0) {
                                          return new NoRebindPresentation(columnUi.present(human, display, observer));
                                      }
                                      if (index == 1) {
                                          return columnUi1.bind(condition).present(human, display, observer);
                                      }
                                      return null;
                                  }

                                  @Override
                                  public Presentation resize(float width, float height, Presentation basis) {
                                      final float width1 = width;
                                      final float height1 = height;
                                      final Presentation original = basis;
                                      return new ResizePresentation(width1, height1, original);
                                  }
                              }));
                    }
                });
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
