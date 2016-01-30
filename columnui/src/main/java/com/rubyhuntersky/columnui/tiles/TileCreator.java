package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.TextStylet;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class TileCreator {

    public static Tui1<String> textTile1(final TextStylet textStylet) {
        return Tui1.create(new Tui1.OnBind<String>() {
            @NonNull
            @Override
            public TileUi onBind(String condition) {
                return Creator.textTile(condition, textStylet);
            }
        });
    }
}
