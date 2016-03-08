package com.rubyhuntersky.columnui.tiles.operations;

import com.rubyhuntersky.columnui.Human;
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

public class ExpandLeftTileOperation1 extends TileOperation1 {
    @Override
    public Tile0 apply0(final Tile0 base, final Tile0 expansion) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(Presenter<Mosaic> presenter) {
                final Human human = presenter.getHuman();
                final Mosaic mosaic = presenter.getDisplay();
                final ShiftMosaic baseShift = mosaic.withShift();
                final ShiftMosaic expansionShift = mosaic.withShift();
                final Presentation presentBase = base.present(human, baseShift, presenter);
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
}
