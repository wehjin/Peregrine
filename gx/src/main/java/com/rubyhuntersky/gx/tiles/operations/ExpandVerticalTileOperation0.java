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

public class ExpandVerticalTileOperation0 extends TileOperation0 {
    private final Sizelet padlet;

    public ExpandVerticalTileOperation0(Sizelet padlet) {

        this.padlet = padlet;
    }

    @Override
    public Tile0 apply(final Tile0 base) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final ShiftMosaic baseMosaic = presenter.getDisplay().withShift();
                final Presentation basePresentation = base.present(human, baseMosaic, presenter);
                final float baseHeight = basePresentation.getHeight();
                final float padding = padlet.toFloat(human, baseHeight);
                baseMosaic.setShift(0, padding);
                final float width = basePresentation.getWidth();
                final float height = baseHeight + 2 * padding;
                presenter.addPresentation(new ResizePresentation(width, height, basePresentation));
            }
        });
    }
}
