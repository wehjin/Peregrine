package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.ui.Ui1;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Cui1<C> implements Ui1<Column, C> {

    private Cui1() {
    }

    public abstract ColumnUi bind(C condition);

    public Cui1<C> padBottom(final Sizelet padlet) {
        final Cui1<C> ui1 = this;
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).padBottom(padlet);
            }
        }, ui1.getStartCondition());
    }

    public Cui1<C> padHorizontal(final Sizelet padlet) {
        final Cui1<C> ui1 = this;
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).padHorizontal(padlet);
            }
        }, ui1.getStartCondition());
    }

    public Cui1<C> placeBefore(final ColumnUi columnUi, final int elevate) {
        final Cui1<C> ui1 = this;
        return Cui1.create(new OnBind<C>() {
            @NonNull
            @Override
            public ColumnUi onBind(C condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).placeBefore(columnUi, elevate);
            }
        }, ui1.getStartCondition());
    }

    public static <C> Cui1<C> create(final OnBind<C> onBind, final C startCondition) {
        return new Cui1<C>() {
            @Override
            public Presentation present(Human human, Column display, Observer observer) {
                return bind(startCondition).present(human, display, observer);
            }

            @Override
            public C getStartCondition() {
                return startCondition;
            }

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

