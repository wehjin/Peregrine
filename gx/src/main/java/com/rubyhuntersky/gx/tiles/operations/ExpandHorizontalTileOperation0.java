package com.rubyhuntersky.gx.tiles.operations;

import com.rubyhuntersky.gx.basics.Sizelet;
import com.rubyhuntersky.gx.Human;
import com.rubyhuntersky.gx.presentations.Presentation;
import com.rubyhuntersky.gx.presentations.ResizePresentation;
import com.rubyhuntersky.gx.presenters.OnPresent;
import com.rubyhuntersky.gx.presenters.Presenter;
import com.rubyhuntersky.gx.tiles.Mosaic;
import com.rubyhuntersky.gx.tiles.ShiftMosaic;
import com.rubyhuntersky.gx.tiles.Tile0;

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
    public Tile0 apply(final Tile0 base) {
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
