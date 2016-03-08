package com.rubyhuntersky.gx.tiles.operations;

import com.rubyhuntersky.gx.client.Observer;
import com.rubyhuntersky.gx.client.Reaction;
import com.rubyhuntersky.gx.client.Presentation;
import com.rubyhuntersky.gx.internal.presenters.OnPresent;
import com.rubyhuntersky.gx.internal.presenters.Presenter;
import com.rubyhuntersky.gx.tiles.Mosaic;
import com.rubyhuntersky.gx.tiles.Tile0;

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
