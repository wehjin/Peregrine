package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.bars.BarUi;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.operations.ToColumnOperation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class Tile0 implements Ui<Mosaic> {

    abstract public Presentation present(Human human, Mosaic mosaic, Observer observer);

    public Tile0 name(final String name) {
        return create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(final Presenter<Mosaic> presenter) {
                final Presentation presentation = Tile0.this.present(presenter.getHuman(),
                                                                      presenter.getDisplay(),
                                                                      new Observer() {
                                                                          @Override
                                                                          public void onReaction(Reaction reaction) {
                                                                              reaction.setSource(name);
                                                                              presenter.onReaction(reaction);
                                                                          }

                                                                          @Override
                                                                          public void onEnd() {
                                                                              presenter.onEnd();
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

    public Tile0 expandLeft(final Tile0 expansion) {
        return create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final Mosaic mosaic = presenter.getDisplay();
                final ShiftMosaic baseShift = mosaic.withShift();
                final ShiftMosaic expansionShift = mosaic.withShift();
                final Presentation presentBase = Tile0.this.present(human, baseShift, presenter);
                final Presentation presentExpansion = expansion.present(human, expansionShift, presenter);
                final float height = Math.max(presentBase.getHeight(), presentExpansion.getHeight());
                baseShift.setShift(presentExpansion.getWidth(), (height - presentBase.getHeight()) * .5f);
                expansionShift.setShift(0, (height - presentExpansion.getHeight()) * .5f);
                presenter.addPresentation(presentExpansion);
                presenter.addPresentation(new ResizePresentation(presentExpansion.getWidth() + presentBase.getWidth(),
                                                                 height,
                                                                 presentBase));
            }
        });
    }

    public Tile0 expandVertical(final Sizelet padlet) {
        return create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final ShiftMosaic shiftingDisplay = presenter.getDisplay().withShift();
                final Presentation basePresentation = Tile0.this.present(human, shiftingDisplay, presenter);
                final float baseHeight = basePresentation.getHeight();
                final float padding = padlet.toFloat(human, baseHeight);
                shiftingDisplay.setShift(0, padding);
                presenter.addPresentation(new ResizePresentation(basePresentation.getWidth(), baseHeight + 2 * padding,
                                                                 basePresentation));
            }
        });
    }

    public Tile0 expandHorizontal(final Sizelet padlet) {
        return create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final ShiftMosaic shiftingDisplay = presenter.getDisplay().withShift();
                final Presentation basePresentation = Tile0.this.present(human, shiftingDisplay, presenter);
                final float baseWidth = basePresentation.getWidth();
                final float padding = padlet.toFloat(human, baseWidth);
                shiftingDisplay.setShift(padding, 0);
                presenter.addPresentation(new ResizePresentation(baseWidth + 2 * padding, basePresentation.getHeight(),
                                                                 basePresentation));
            }
        });
    }

    public BarUi toBar() {
        return BarUi.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
                final Mosaic mosaic = new Mosaic(bar.relatedWidth, bar.fixedHeight, bar.elevation, bar);
                final ShiftMosaic frameShiftTile = mosaic.withShift();
                final Presentation presentation = present(presenter.getHuman(), frameShiftTile, presenter);
                final float presentationHeight = presentation.getHeight();
                final float extraHeight = bar.fixedHeight - presentationHeight;
                final float anchor = .5f;
                frameShiftTile.setShift(0, extraHeight * anchor);
                presenter.addPresentation(presentation);
            }
        });
    }

    public ColumnUi toColumn() {
        return new ToColumnOperation().applyTo(this);
    }

    public static Tile0 create(final OnPresent<Mosaic> onPresent) {
        return new Tile0() {
            @Override
            public Presentation present(Human human, final Mosaic mosaic, Observer observer) {
                final BasePresenter<Mosaic> presenter = new BasePresenter<Mosaic>(human, mosaic, observer) {
                    @Override
                    public float getWidth() {
                        float union = 0;
                        for (Presentation presentation : presentations) {
                            union = Math.max(union, presentation.getWidth());
                        }
                        return union;
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
