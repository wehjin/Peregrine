package com.rubyhuntersky.columnui.binders;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.tiles.TileUi;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface OnBind<T> {
    @NonNull
    TileUi onBind(T condition);
}
