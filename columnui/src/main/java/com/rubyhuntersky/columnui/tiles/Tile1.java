package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.operations.ToColumnOperation;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tile1<C> implements Ui1<Mosaic, C> {

    private Tile1() {
    }

    @Override
    public abstract Tile0 bind(C condition);

    public Tile1<C> name(final String name) {
        return create(new OnBind<C>() {
            @NonNull
            @Override
            public Tile0 onBind(C condition) {
                return Tile1.this.bind(condition).name(name);
            }
        });
    }

    public Tile1<C> expandLeft(final Tile0 expansion) {
        return create(new OnBind<C>() {
            @NonNull
            @Override
            public Tile0 onBind(C condition) {
                return Tile1.this.bind(condition).expandLeft(expansion);
            }
        });
    }

    public ColumnUi1<C> toColumn() {
        return new ToColumnOperation().applyTo(this);
    }

    public static <C> Tile1<C> create(final OnBind<C> onBind) {
        return new Tile1<C>() {

            @Override
            public Tile0 bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        Tile0 onBind(C condition);
    }
}
