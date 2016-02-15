package com.rubyhuntersky.columnui.tiles.operations;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.tiles.Mosaic;
import com.rubyhuntersky.columnui.tiles.ShiftMosaic;
import com.rubyhuntersky.columnui.tiles.Tile0;

/**
 * @author wehjin
 * @since 2/14/16.
 */

public class ExpandHorizontalTileOperation0 extends TileOperation0 {
    private final Sizelet padlet;

    public ExpandHorizontalTileOperation0(Sizelet padlet) {

        this.padlet = padlet;
    }

    @Override
    public Tile0 apply0(final Tile0 base) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final ShiftMosaic shiftingDisplay = presenter.getDisplay().withShift();
                final Presentation basePresentation = base.present(human, shiftingDisplay, presenter);
                final float baseWidth = basePresentation.getWidth();
                final float padding = padlet.toFloat(human, baseWidth);
                shiftingDisplay.setShift(padding, 0);
                presenter.addPresentation(new ResizePresentation(baseWidth + 2 * padding, basePresentation.getHeight(),
                                                                 basePresentation));
            }
        });
    }
}
