package com.rubyhuntersky.columnui.operations;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.columns.Div1;
import com.rubyhuntersky.columnui.columns.Div2;
import com.rubyhuntersky.columnui.columns.Div3;
import com.rubyhuntersky.columnui.columns.Div4;

/**
 * @author wehjin
 * @since 2/9/16.
 */

public class ExpandBottomWithFutureDivOperation {

    public Div1<Div0> applyTo(final Div0 div) {
        return Div1.create(new Div1.OnBind<Div0>() {
            @NonNull
            @Override
            public Div0 onBind(Div0 condition) {
                return div.expandDown(condition);
            }
        });
    }

    public <C> Div2<C, Div0> applyTo(final Div1<C> div1) {
        return Div2.create(new Div2.OnBind<C, Div0>() {
            @Override
            public Div1<Div0> onBind(C condition) {
                return div1.bind(condition).expandDown();
            }
        });
    }

    public <C1, C2> Div3<C1, C2, Div0> applyTo(final Div2<C1, C2> div2) {
        return Div3.create(new Div3.OnBind<C1, C2, Div0>() {
            @Override
            public Div2<C2, Div0> onBind(C1 condition) {
                return div2.bind(condition).expandDown();
            }
        });
    }

    public <C1, C2, C3> Div4<C1, C2, C3, Div0> applyTo(final Div3<C1, C2, C3> div3) {
        return Div4.create(new Div4.OnBind<C1, C2, C3, Div0>() {
            @Override
            public Div3<C2, C3, Div0> onBind(C1 condition) {
                return div3.bind(condition).expandDown();
            }
        });
    }
}
