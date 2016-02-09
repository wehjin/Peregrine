package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.bars.BarUi;
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

abstract public class TileUi implements Ui<Tile> {

    abstract public Presentation present(Human human, Tile tile, Observer observer);

    public TileUi name(final String name) {
        return create(new OnPresent<Tile>() {
            @Override
            public void onPresent(final Presenter<Tile> presenter) {
                final Presentation presentation = TileUi.this.present(presenter.getHuman(),
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

    public TileUi expandLeft(final TileUi expansion) {
        return create(new OnPresent<Tile>() {
            @Override
            public void onPresent(Presenter<Tile> presenter) {
                final Human human = presenter.getHuman();
                final Tile tile = presenter.getDisplay();
                final ShiftTile baseShift = tile.withShift();
                final ShiftTile expansionShift = tile.withShift();
                final Presentation presentBase = TileUi.this.present(human, baseShift, presenter);
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

    public BarUi toBar() {
        return BarUi.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
                final Tile tile = new Tile(bar.relatedWidth, bar.fixedHeight, bar.elevation, bar);
                final ShiftTile frameShiftTile = tile.withShift();
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

    public static TileUi create(final OnPresent<Tile> onPresent) {
        return new TileUi() {
            @Override
            public Presentation present(Human human, final Tile tile, Observer observer) {
                final BasePresenter<Tile> presenter = new BasePresenter<Tile>(human, tile, observer) {
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
