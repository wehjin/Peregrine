package com.rubyhuntersky.gx.tiles;

import com.rubyhuntersky.gx.Observer;
import com.rubyhuntersky.gx.bars.BarExtender;
import com.rubyhuntersky.gx.bars.Span0;
import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.columns.Div0;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.operations.ToColumnOperation;
import com.rubyhuntersky.gx.presentations.Presentation;
import com.rubyhuntersky.gx.presenters.BasePresenter;
import com.rubyhuntersky.gx.presenters.OnPresent;
import com.rubyhuntersky.gx.presenters.Presenter;
import com.rubyhuntersky.gx.tiles.operations.ExpandDownTileOperation1;
import com.rubyhuntersky.gx.tiles.operations.ExpandHorizontalTileOperation0;
import com.rubyhuntersky.gx.tiles.operations.ExpandLeftTileOperation1;
import com.rubyhuntersky.gx.tiles.operations.ExpandVerticalTileOperation0;
import com.rubyhuntersky.gx.tiles.operations.NameTileOperation0;
import com.rubyhuntersky.gx.ui.Ui;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class Tile0 implements Ui<Mosaic> {

    abstract public Presentation present(Human human, Mosaic mosaic, Observer observer);

    public Tile0 name(final String name) {
        return new NameTileOperation0(name).apply(this);
    }

    public Tile0 expandLeft(final Tile0 expansion) {
        return new ExpandLeftTileOperation1().apply0(this, expansion);
    }

    public Tile0 expandDown(final Tile0 expansion) {
        return new ExpandDownTileOperation1().apply0(this, expansion);
    }

    public <C> Tile1<C> expandDown(final Tile1<C> expansion) {
        return new ExpandDownTileOperation1().apply1(this, expansion);
    }

    public Tile0 expandVertical(final Sizelet padlet) {
        return new ExpandVerticalTileOperation0(padlet).apply(this);
    }

    public Tile0 expandHorizontal(final Sizelet padlet) {
        return new ExpandHorizontalTileOperation0(padlet).apply(this);
    }

    public Span0 toBar() {
        return Span0.create(new OnPresent<BarExtender>() {
            @Override
            public void onPresent(Presenter<BarExtender> presenter) {
                final BarExtender bar = presenter.getDisplay();
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

    public Div0 toColumn() {
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