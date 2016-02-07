package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class TileUi1<C> implements Ui1<Tile, C> {

    private TileUi1() {
    }

    @Override
    public abstract BoundTui1 bind(C condition);

    public ColumnUi1<C> toColumn() {
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return TileUi1.this.bind(condition).toColumn();
            }
        });
    }

    public static <C> TileUi1<C> create(final OnBind<C> onBind) {
        return new TileUi1<C>() {

            @Override
            public BoundTui1 bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        BoundTui1 onBind(C condition);
    }
}
