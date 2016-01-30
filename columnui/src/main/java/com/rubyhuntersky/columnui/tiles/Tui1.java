package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tui1<T> implements Bindable<T, TileUi> {

    private Tui1() {
    }

    public abstract TileUi bind(T condition);

    public Cui1<T> toColumn() {
        final Tui1<T> tui1 = this;
        return Cui1.create(new Cui1.OnBind<T>() {
            @NonNull
            @Override
            public ColumnUi onBind(T condition) {
                return tui1.bind(condition).toColumn();
            }
        });
    }

    public static <T> Tui1<T> create(final OnBind<T> onBind) {
        return new Tui1<T>() {
            @Override
            public TileUi bind(T condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<T> {
        @NonNull
        TileUi onBind(T condition);
    }
}
