package com.rubyhuntersky.columnui.bars;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.columns.Div1;
import com.rubyhuntersky.columnui.tiles.Tile0;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class BarUi1<C> implements Ui1<Bar, C> {

    public abstract BarUi bind(C condition);

    public static <C> BarUi1<C> create(final OnBind<C> onBind) {
        return new BarUi1<C>() {
            @Override
            public BarUi bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public BarUi1<C> expandStart(final Tile0 tile0) {
        return BarUi1.create(new OnBind<C>() {
            @Override
            public BarUi onBind(C condition) {
                return BarUi1.this.bind(condition).expandStart(tile0);
            }
        });
    }

    public Div1<C> toColumn(final Sizelet heightlet) {
        return Div1.create(new Div1.OnBind<C>() {
            @NonNull
            @Override
            public Div0 onBind(final C condition) {
                return BarUi1.this.bind(condition).toColumn(heightlet);
            }
        });
    }

    public interface OnBind<C> {
        BarUi onBind(C condition);
    }
}
