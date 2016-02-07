package com.rubyhuntersky.columnui.columns;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class ColumnUi1<C> implements Ui1<Column, C> {

    private ColumnUi1() {
    }

    @Override
    public abstract ColumnUi bind(C condition);

    public ColumnUi1<C> padBottom(final Sizelet padlet) {
        // TODO Test
        final ColumnUi1<C> ui1 = this;
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return ui1.bind(condition).padBottom(padlet);
            }
        });
    }

    public ColumnUi1<C> padHorizontal(final Sizelet padlet) {
        // TODO Test
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return ColumnUi1.this.bind(condition).padHorizontal(padlet);
            }
        });
    }

    public ColumnUi1<C> placeBefore(final ColumnUi columnUi, final int elevate) {
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(final C condition) {
                return ColumnUi1.this.bind(condition).placeBefore(columnUi, elevate);
            }
        });
    }


    public ColumnUi1<C> expandBottom(final ColumnUi expansion) {
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                return ColumnUi1.this.bind(condition).expandBottom(expansion);
            }
        });
    }

    public <C2> ColumnUi2<C, C2> expandBottom(final ColumnUi1<C2> expansion) {
        return ColumnUi2.create((new ColumnUi2.OnBind<C, C2>() {
            @Override
            public ColumnUi1<C2> onBind(C condition) {
                return ColumnUi1.this.bind(condition).expandBottom(expansion);
            }
        }));
    }

    public ColumnUi1<C> expandVertical(final Sizelet heightlet) {
        return ColumnUi1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                return ColumnUi1.this.bind(condition).expandVertical(heightlet);
            }
        });
    }

    public static <C> ColumnUi1<C> create(final OnBind<C> onBind) {
        return new ColumnUi1<C>() {
            @Override
            public ColumnUi bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        ColumnUi onBind(C condition);
    }

}

