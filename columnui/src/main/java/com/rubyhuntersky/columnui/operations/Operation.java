package com.rubyhuntersky.columnui.operations;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.columns.ColumnUi3;
import com.rubyhuntersky.columnui.columns.ColumnUi4;
import com.rubyhuntersky.columnui.columns.ColumnUi5;

/**
 * @author wehjin
 * @since 2/9/16.
 */
abstract public class Operation {

    abstract public ColumnUi applyTo(final ColumnUi previous);

    public <C> ColumnUi1<C> applyTo(final ColumnUi1<C> previous) {
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2> ColumnUi2<C1, C2> applyTo(final ColumnUi2<C1, C2> previous) {
        return ColumnUi2.create(new ColumnUi2.OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C2> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3> ColumnUi3<C1, C2, C3> applyTo(final ColumnUi3<C1, C2, C3> previous) {
        return ColumnUi3.create(new ColumnUi3.OnBind<C1, C2, C3>() {
            @Override
            public ColumnUi2<C2, C3> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3, C4> ColumnUi4<C1, C2, C3, C4> applyTo(final ColumnUi4<C1, C2, C3, C4> previous) {
        return ColumnUi4.create(new ColumnUi4.OnBind<C1, C2, C3, C4>() {
            @Override
            public ColumnUi3<C2, C3, C4> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3, C4, C5> ColumnUi5<C1, C2, C3, C4, C5> applyTo(final ColumnUi5<C1, C2, C3, C4, C5> previous) {
        return ColumnUi5.create(new ColumnUi5.OnBind<C1, C2, C3, C4, C5>() {
            @Override
            public ColumnUi4<C2, C3, C4, C5> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }
}
