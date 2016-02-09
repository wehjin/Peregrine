package com.rubyhuntersky.columnui.operations;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi1;
import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.columns.ColumnUi3;
import com.rubyhuntersky.columnui.columns.ColumnUi4;

/**
 * @author wehjin
 * @since 2/9/16.
 */
abstract public class Operation {

    abstract public ColumnUi applyTo(final ColumnUi div);

    public <C> ColumnUi1<C> applyTo(final ColumnUi1<C> div1) {
        return ColumnUi1.create(new ColumnUi1.OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                return Operation.this.applyTo(div1.bind(condition));
            }
        });
    }

    public <C1, C2> ColumnUi2<C1, C2> applyTo(final ColumnUi2<C1, C2> div2) {
        return ColumnUi2.create(new ColumnUi2.OnBind<C1, C2>() {
            @Override
            public ColumnUi1<C2> onBind(C1 condition) {
                return Operation.this.applyTo(div2.bind(condition));
            }
        });
    }

    public <C1, C2, C3> ColumnUi3<C1, C2, C3> applyTo(final ColumnUi3<C1, C2, C3> div3) {
        return ColumnUi3.create(new ColumnUi3.OnBind<C1, C2, C3>() {
            @Override
            public ColumnUi2<C2, C3> onBind(C1 condition) {
                return Operation.this.applyTo(div3.bind(condition));
            }
        });
    }

    public <C1, C2, C3, C4> ColumnUi4<C1, C2, C3, C4> applyTo(final ColumnUi4<C1, C2, C3, C4> div4) {
        return ColumnUi4.create(new ColumnUi4.OnBind<C1, C2, C3, C4>() {
            @Override
            public ColumnUi3<C2, C3, C4> onBind(C1 condition) {
                return Operation.this.applyTo(div4.bind(condition));
            }
        });
    }
}
