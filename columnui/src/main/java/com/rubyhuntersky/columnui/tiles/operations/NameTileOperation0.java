package com.rubyhuntersky.columnui.tiles.operations;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.tiles.Mosaic;
import com.rubyhuntersky.columnui.tiles.Tile0;

/**
 * @author wehjin
 * @since 2/14/16.
 */

public class NameTileOperation0 extends TileOperation0 {
    private final String name;

    public NameTileOperation0(String name) {

        this.name = name;
    }

    @Override
    public Tile0 apply(final Tile0 base) {
        return Tile0.create(new OnPresent<Mosaic>() {
            @Override
            public void onPresent(final Presenter<Mosaic> presenter) {
                final Presentation presentation = base.present(presenter.getHuman(),
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
}
