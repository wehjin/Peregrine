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

public class ExpandBottomWithFutureDivOperation {

    public ColumnUi1<ColumnUi> applyTo(final ColumnUi div) {
        return ColumnUi1.create(new ColumnUi1.OnBind<ColumnUi>() {
            @NonNull
            @Override
            public ColumnUi onBind(ColumnUi condition) {
                return div.expandBottom(condition);
            }
        });
    }

    public <C> ColumnUi2<C, ColumnUi> applyTo(final ColumnUi1<C> div1) {
        return ColumnUi2.create(new ColumnUi2.OnBind<C, ColumnUi>() {
            @Override
            public ColumnUi1<ColumnUi> onBind(C condition) {
                return div1.bind(condition).expandBottom();
            }
        });
    }

    public <C1, C2> ColumnUi3<C1, C2, ColumnUi> applyTo(final ColumnUi2<C1, C2> div2) {
        return ColumnUi3.create(new ColumnUi3.OnBind<C1, C2, ColumnUi>() {
            @Override
            public ColumnUi2<C2, ColumnUi> onBind(C1 condition) {
                return div2.bind(condition).expandBottom();
            }
        });
    }

    public <C1, C2, C3> ColumnUi4<C1, C2, C3, ColumnUi> applyTo(final ColumnUi3<C1, C2, C3> div3) {
        return ColumnUi4.create(new ColumnUi4.OnBind<C1, C2, C3, ColumnUi>() {
            @Override
            public ColumnUi3<C2, C3, ColumnUi> onBind(C1 condition) {
                return div3.bind(condition).expandBottom();
            }
        });
    }
}
