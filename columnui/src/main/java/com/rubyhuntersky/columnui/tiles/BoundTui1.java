package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation1;
import com.rubyhuntersky.columnui.presentations.ResizePresentation1;
import com.rubyhuntersky.columnui.ui.BoundUi1;
import com.rubyhuntersky.columnui.ui.PresentationMaker;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class BoundTui1<C> extends TileUi implements BoundUi1<Tile, C> {

    @Override
    public abstract Presentation1<C> present(Human human, Tile tile, Observer observer);

    public Presentation1<C> presentToColumn(Human human, Column column, Observer observer) {
        return super.presentToColumn(human, column, observer, new PresentationMaker<Presentation1<C>, Tile>() {
            @Override
            public Presentation1<C> present(Human human, Tile display, Observer observer, int index) {
                return BoundTui1.this.present(human, display, observer);
            }

            @Override
            public Presentation1<C> resize(float width, float height, Presentation1<C> basis) {
                return new ResizePresentation1<>(width, height, basis);
            }
        });
    }
}
