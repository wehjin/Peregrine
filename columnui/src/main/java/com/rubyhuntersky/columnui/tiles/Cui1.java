package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.NoRebindPresentation1;
import com.rubyhuntersky.columnui.presentations.PairPresentation1;
import com.rubyhuntersky.columnui.presentations.Presentation1;
import com.rubyhuntersky.columnui.presentations.ResizePresentation1;
import com.rubyhuntersky.columnui.ui.PresentationMaker;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Cui1<C> implements Ui1<Column, C> {

    private Cui1() {
    }

    @Override
    public abstract BoundCui1<C> bind(C condition);

    public Cui1<C> padBottom(final Sizelet padlet) {
        final Cui1<C> ui1 = this;
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1<C> onBind(final C condition) {
                return BoundCui1.create(condition, new BoundCui1.OnPresent1<C>() {
                    @Override
                    public Presentation1<C> onPresent(Human human, Column column, Observer observer) {
                        return ColumnUi.presentWithBottomPadding(padlet, human, column, observer,
                              new PresentationMaker<Presentation1<C>, Column>() {
                                  @Override
                                  public Presentation1<C> present(Human human, Column display, Observer observer,
                                        int index) {
                                      return ui1.bind(condition).present(human, display, observer);
                                  }

                                  @Override
                                  public Presentation1<C> resize(float width, float height, Presentation1<C> basis) {
                                      return new ResizePresentation1<>(width, height, basis);
                                  }

                              });
                    }
                });
            }
        });
    }

    public Cui1<C> padHorizontal(final Sizelet padlet) {
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1<C> onBind(final C condition) {
                return BoundCui1.create(condition, new BoundCui1.OnPresent1<C>() {
                    @Override
                    public Presentation1<C> onPresent(Human human, Column column, Observer observer) {
                        return ColumnUi.presentWithHorizontalPadding(padlet, human, column, observer,
                              new PresentationMaker<Presentation1<C>, Column>() {
                                  @Override
                                  public Presentation1<C> present(Human human, Column display, Observer observer,
                                        int index) {
                                      return Cui1.this.bind(condition).present(human, display, observer);
                                  }

                                  @Override
                                  public Presentation1<C> resize(float width, float height, Presentation1<C> basis) {
                                      return new ResizePresentation1<>(width, height, basis);
                                  }

                              });
                    }
                });
            }
        });
    }

    public Cui1<C> placeBefore(final ColumnUi columnUi, final int elevate) {
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1<C> onBind(final C condition) {
                return BoundCui1.create(condition, new BoundCui1.OnPresent1<C>() {
                    @Override
                    public Presentation1<C> onPresent(Human human, Column column, Observer observer) {
                        return new PairPresentation1<>(
                              ColumnUi.presentFirstBeforeSecond(elevate, human, column, observer,
                                    new PresentationMaker<Presentation1<C>, Column>() {
                                        @Override
                                        public Presentation1<C> present(Human human, Column display, Observer observer,
                                              int index) {
                                            if (index == 0) {
                                                return Cui1.this.bind(condition).present(human, display, observer);
                                            }
                                            if (index == 1) {
                                                return new NoRebindPresentation1<>(
                                                      columnUi.present(human, display, observer));
                                            }
                                            return null;
                                        }

                                        @Override
                                        public Presentation1<C> resize(float width, float height,
                                              Presentation1<C> basis) {
                                            return null;
                                        }

                                    }));
                    }
                });
            }
        });
    }

    public static <C> Cui1<C> create(final OnBind<C> onBind) {
        return new Cui1<C>() {
            @Override
            public BoundCui1<C> bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        BoundCui1<C> onBind(C condition);
    }

}

