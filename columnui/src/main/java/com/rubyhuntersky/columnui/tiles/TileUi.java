package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.bars.BarUi;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.ui.PresentationMaker;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class TileUi implements Ui<Tile> {

    abstract public Presentation present(Human human, Tile tile, Observer observer);

    public BarUi toBar() {
        return BarUi.create(new OnPresent<Bar>() {
            @Override
            public void onPresent(Presenter<Bar> presenter) {
                final Bar bar = presenter.getDisplay();
                final Tile tile = new Tile(bar.relatedWidth, bar.fixedHeight, bar.elevation, bar);
                final FrameShiftTile frameShiftTile = tile.withShift();
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
        return ColumnUi.create(new OnPresent<Column>() {
            @Override
            public void onPresent(Presenter<Column> presenter) {
                presenter.addPresentation(presentToColumn(presenter.getHuman(), presenter.getDisplay(), presenter,
                      new PresentationMaker<Presentation, Tile>() {
                          @Override
                          public Presentation present(Human human, Tile display, Observer observer, int index) {
                              return TileUi.this.present(human, display, observer);
                          }

                          @Override
                          public Presentation resize(float width, float height, Presentation basis) {
                              return new ResizePresentation(width, height, basis);
                          }
                      }));
            }
        });
    }

    public <P extends Presentation> P presentToColumn(Human human, Column column, Observer observer,
          PresentationMaker<P, Tile> maker) {
        final Tile tile = new Tile(column.fixedWidth, column.relatedHeight, column.elevation, column);
        final FrameShiftTile frameShiftTile = tile.withShift();
        final P presentation = maker.present(human, frameShiftTile, observer, 0);
        final float presentationWidth = presentation.getWidth();
        final float extraWidth = column.fixedWidth - presentationWidth;
        final float anchor = .5f;
        frameShiftTile.setShift(extraWidth * anchor, 0);
        return maker.resize(column.fixedWidth, presentation.getHeight(), presentation);
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
