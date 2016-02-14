package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.operations.ToColumnOperation;
import com.rubyhuntersky.columnui.ui.Ui2;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tile2<C1, C2> implements Ui2<Mosaic, C1, C2> {

    private Tile2() {
    }

    @Override
    public abstract Tile1<C2> bind(C1 condition);

    public Tile2<C1, C2> name(final String name) {
        return create(new OnBind<C1, C2>() {
            @NonNull
            @Override
            public Tile1<C2> onBind(C1 condition) {
                return Tile2.this.bind(condition).name(name);
            }
        });
    }

    public Tile2<C1, C2> expandLeft(final Tile0 expansion) {
        return create(new OnBind<C1, C2>() {
            @NonNull
            @Override
            public Tile1<C2> onBind(C1 condition) {
                return Tile2.this.bind(condition).expandLeft(expansion);
            }
        });
    }

    public Tile2<C1, C2> expandBottom(final Tile0 expansion) {
        return create(new OnBind<C1, C2>() {
            @NonNull
            @Override
            public Tile1<C2> onBind(C1 condition) {
                return Tile2.this.bind(condition).expandBottom(expansion);
            }
        });
    }

    public ColumnUi2<C1, C2> toColumn() {
        return new ToColumnOperation().applyTo(this);
    }

    public static <C1, C2> Tile2<C1, C2> create(final OnBind<C1, C2> onBind) {
        return new Tile2<C1, C2>() {

            @Override
            public Tile1<C2> bind(C1 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C1, C2> {
        @NonNull
        Tile1<C2> onBind(C1 condition);
    }
}
