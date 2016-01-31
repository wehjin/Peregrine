package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.ui.Ui;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class TileCreator {

    public interface ConditionedUiSource<D, C> {
        Ui<D> getUi(C condition);
    }

    public static Tui1<String> textTile1(final TextStylet textStylet) {
        final ConditionedUiSource<Tile, String> uiSource = new ConditionedUiSource<Tile, String>() {
            @Override
            public Ui<Tile> getUi(String condition) {
                return Creator.textTile(condition, textStylet);
            }
        };
        return Tui1.create(new Tui1.OnBind<String>() {
            @NonNull
            @Override
            public BoundTui1<String> onBind(final String condition) {
                return new RecreateOnRebindBoundTui1<>(uiSource, condition);
            }
        });
    }

}
