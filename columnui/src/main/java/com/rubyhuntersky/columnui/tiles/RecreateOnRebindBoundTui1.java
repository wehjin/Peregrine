package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/30/16.
 */
class RecreateOnRebindBoundTui1<C> extends BoundTui1 {
    private final TileCreator.ConditionedUiSource<Tile, C> conditionedUiSource;
    private final C startCondition;

    public RecreateOnRebindBoundTui1(TileCreator.ConditionedUiSource<Tile, C> conditionedUiSource, C startCondition) {
        this.conditionedUiSource = conditionedUiSource;
        this.startCondition = startCondition;
    }

    @Override
    public Presentation present(final Human human, final Tile display, final Observer observer) {

        return new Presentation() {
            Presentation presentation = conditionedUiSource.getUi(startCondition).present(human, display, observer);

            @Override
            public float getWidth() {
                return presentation.getWidth();
            }

            @Override
            public float getHeight() {
                return presentation.getHeight();
            }

            @Override
            public boolean isCancelled() {
                return presentation.isCancelled();
            }

            @Override
            public void cancel() {
                presentation.cancel();
            }

        };
    }

}
