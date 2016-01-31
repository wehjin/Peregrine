package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.ResizePresentation;
import com.rubyhuntersky.columnui.ui.PresentationMaker;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class BoundTui1 extends TileUi implements Ui<Tile> {

    @Override
    public abstract Presentation present(Human human, Tile tile, Observer observer);

    public Presentation presentToColumn(Human human, Column column, Observer observer) {
        return super.presentToColumn(human, column, observer, new PresentationMaker<Presentation, Tile>() {
            @Override
            public Presentation present(Human human, Tile display, Observer observer, int index) {
                return BoundTui1.this.present(human, display, observer);
            }

            @Override
            public Presentation resize(float width, float height, Presentation basis) {
                return new ResizePresentation(width, height, basis);
            }
        });
    }
}
