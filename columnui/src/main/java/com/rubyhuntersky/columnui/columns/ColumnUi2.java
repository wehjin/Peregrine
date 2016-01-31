package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.basics.Sizelet;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class ColumnUi2<C1, C2> {

    public abstract ColumnUi1<C1> bind(C2 condition);

    public static <C1, C2> ColumnUi2<C1, C2> create(final OnBind<C1, C2> onBind) {
        return new ColumnUi2<C1, C2>() {
            @Override
            public ColumnUi1<C1> bind(C2 condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public ColumnUi2<C1, C2> padBottom(final Sizelet heightlet) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).padBottom(heightlet);
            }
        });
    }

    public ColumnUi2<C1, C2> padHorizontal(final Sizelet padlet) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).padHorizontal(padlet);
            }
        });
    }

    public ColumnUi2<C1, C2> placeBefore(final ColumnUi columnUi, final int gap) {
        return create(new OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C1> onBind(C2 condition) {
                return ColumnUi2.this.bind(condition).placeBefore(columnUi, gap);
            }
        });
    }


    public interface OnBind<C1, C2> {
        ColumnUi1<C1> onBind(C2 condition);
    }
}
