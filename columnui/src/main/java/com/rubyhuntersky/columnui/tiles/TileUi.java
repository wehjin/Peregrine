package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.BasePresenter;
import com.rubyhuntersky.columnui.BaseUi;
import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.OnPresent;
import com.rubyhuntersky.columnui.Presentation;
import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/28/16.
 */

abstract public class TileUi extends BaseUi<Tile> {

    abstract public Presentation present(Human human, Tile tile, Observer observer);

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
