package com.rubyhuntersky.columnui.tiles;

import android.util.Log;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presentations.Presentation1;

/**
 * @author wehjin
 * @since 1/30/16.
 */
class RecreateOnRebindBoundTui1<C> extends BoundTui1<C> {
    public static final String TAG = RecreateOnRebindBoundTui1.class.getSimpleName();
    private final TileCreator.ConditionedUiSource<Tile, C> conditionedUiSource;
    private final C startCondition;

    public RecreateOnRebindBoundTui1(TileCreator.ConditionedUiSource<Tile, C> conditionedUiSource, C startCondition) {
        this.conditionedUiSource = conditionedUiSource;
        this.startCondition = startCondition;
    }

    @Override
    public Presentation1<C> present(final Human human, final Tile display, final Observer observer) {

        return new Presentation1<C>() {
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

            @Override
            public void rebind(C condition) {
                if (isCancelled()) return;
                Log.d(TAG, "Rebind, recreating for condition: " + condition);
                presentation.cancel();
                presentation = conditionedUiSource.getUi(condition).present(human, display, observer);
            }
        };
    }

    @Override
    public C getCondition() {
        return startCondition;
    }
}
