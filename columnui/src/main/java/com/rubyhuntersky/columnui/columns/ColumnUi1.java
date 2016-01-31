package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.NoRebindPresentation;
import com.rubyhuntersky.columnui.presentations.PairPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.ui.PresentationMaker;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class ColumnUi1<C> implements Ui1<Column, C> {

    private ColumnUi1() {
    }

    @Override
    public abstract BoundCui1 bind(C condition);

    public ColumnUi1<C> padBottom(final Sizelet padlet) {
        final ColumnUi1<C> ui1 = this;
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1 onBind(final C condition) {
                return BoundCui1.create(new BoundCui1.OnPresent1() {
                    @Override
                    public Presentation onPresent(Human human, Column column, Observer observer) {
                        return ColumnUi.presentWithBottomPadding(padlet, human, column, observer,
                              new PresentationMaker<Presentation, Column>() {
                                  @Override
                                  public Presentation present(Human human, Column display, Observer observer,
                                        int index) {
                                      return ui1.bind(condition).present(human, display, observer);
                                  }

                                  @Override
                                  public Presentation resize(float width, float height, Presentation basis) {
                                      final float width1 = width;
                                      final float height1 = height;
                                      final Presentation original = basis;
                                      return new ResizePresentation(width1, height1, original);
                                  }

                              });
                    }
                });
            }
        });
    }

    public ColumnUi1<C> padHorizontal(final Sizelet padlet) {
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1 onBind(final C condition) {
                return BoundCui1.create(new BoundCui1.OnPresent1() {
                    @Override
                    public Presentation onPresent(Human human, Column column, Observer observer) {
                        return ColumnUi.presentWithHorizontalPadding(padlet, human, column, observer,
                              new PresentationMaker<Presentation, Column>() {
                                  @Override
                                  public Presentation present(Human human, Column display, Observer observer,
                                        int index) {
                                      return ColumnUi1.this.bind(condition).present(human, display, observer);
                                  }

                                  @Override
                                  public Presentation resize(float width, float height, Presentation basis) {
                                      final float width1 = width;
                                      final float height1 = height;
                                      final Presentation original = basis;
                                      return new ResizePresentation(width1, height1, original);
                                  }

                              });
                    }
                });
            }
        });
    }

    public ColumnUi1<C> placeBefore(final ColumnUi columnUi, final int elevate) {
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1 onBind(final C condition) {
                return BoundCui1.create(new BoundCui1.OnPresent1() {
                    @Override
                    public Presentation onPresent(Human human, Column column, Observer observer) {
                        return new PairPresentation(ColumnUi.presentFirstBeforeSecond(elevate, human, column, observer,
                              new PresentationMaker<Presentation, Column>() {
                                  @Override
                                  public Presentation present(Human human, Column display, Observer observer,
                                        int index) {
                                      if (index == 0) {
                                          return ColumnUi1.this.bind(condition).present(human, display, observer);
                                      }
                                      if (index == 1) {
                                          return new NoRebindPresentation(columnUi.present(human, display, observer));
                                      }
                                      return null;
                                  }

                                  @Override
                                  public Presentation resize(float width, float height, Presentation basis) {
                                      return null;
                                  }

                              }));
                    }
                });
            }
        });
    }

    public static <C> ColumnUi1<C> create(final OnBind<C> onBind) {
        return new ColumnUi1<C>() {
            @Override
            public BoundCui1 bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        BoundCui1 onBind(C condition);
    }

}

