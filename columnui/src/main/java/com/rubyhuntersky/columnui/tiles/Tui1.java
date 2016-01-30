package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.ui.Ui1;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tui1<C> implements Ui1<Tile, C> {

    private Tui1() {
    }

    @Override
    public abstract TileUi bind(C condition);

    public Cui1<C> toColumn() {
        final Tui1<C> tui1 = this;
        return Cui1.create(new Cui1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                return tui1.bind(condition).toColumn();
            }
        }, tui1.getStartCondition());
    }

    public static <T> Tui1<T> create(final OnBind<T> onBind, final T startCondition) {
        return new Tui1<T>() {

            @Override
            public T getStartCondition() {
                return startCondition;
            }

            @Override
            public TileUi bind(T condition) {
                return onBind.onBind(condition);
            }

            @Override
            public Presentation present(Human human, Tile display, Observer observer) {
                return bind(startCondition).present(human, display, observer);
            }
        };
    }

    public interface OnBind<T> {
        @NonNull
        TileUi onBind(T condition);
    }
}
