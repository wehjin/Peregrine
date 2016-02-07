package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.TextStylet;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class TileCreator {

    public static TileUi1<String> textTile1(final TextStylet textStylet) {
        return TileUi1.create(new TileUi1.OnBind<String>() {
            @NonNull
            @Override
            public TileUi onBind(final String condition) {
                return Creator.textTile(condition, textStylet);
            }
        });
    }

}
