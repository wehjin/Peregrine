package com.rubyhuntersky.columnui.operations;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.columns.Div0;
import com.rubyhuntersky.columnui.columns.Div1;
import com.rubyhuntersky.columnui.columns.Div2;
import com.rubyhuntersky.columnui.columns.Div3;
import com.rubyhuntersky.columnui.columns.Div4;
import com.rubyhuntersky.columnui.columns.Div5;

/**
 * @author wehjin
 * @since 2/9/16.
 */
abstract public class Operation {

    abstract public Div0 applyTo(final Div0 previous);

    public <C> Div1<C> applyTo(final Div1<C> previous) {
        return Div1.create(new Div1.OnBind<C>() {
            @NonNull
            @Override
            public Div0 onBind(C condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2> Div2<C1, C2> applyTo(final Div2<C1, C2> previous) {
        return Div2.create(new Div2.OnBind<C1, C2>() {
            @Override
            public Div1<C2> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3> Div3<C1, C2, C3> applyTo(final Div3<C1, C2, C3> previous) {
        return Div3.create(new Div3.OnBind<C1, C2, C3>() {
            @Override
            public Div2<C2, C3> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3, C4> Div4<C1, C2, C3, C4> applyTo(final Div4<C1, C2, C3, C4> previous) {
        return Div4.create(new Div4.OnBind<C1, C2, C3, C4>() {
            @Override
            public Div3<C2, C3, C4> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }

    public <C1, C2, C3, C4, C5> Div5<C1, C2, C3, C4, C5> applyTo(final Div5<C1, C2, C3, C4, C5> previous) {
        return Div5.create(new Div5.OnBind<C1, C2, C3, C4, C5>() {
            @Override
            public Div4<C2, C3, C4, C5> onBind(C1 condition) {
                return Operation.this.applyTo(previous.bind(condition));
            }
        });
    }
}
