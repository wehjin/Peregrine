package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.TextStylet;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public class TileCreator {

    public static Tile1<String> textTile1(final TextStylet textStylet) {
        return Tile1.create(new Tile1.OnBind<String>() {
            @NonNull
            @Override
            public Tile0 onBind(final String condition) {
                return Creator.textTile(condition, textStylet);
            }
        });
    }

}
