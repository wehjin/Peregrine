package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.ColumnUi;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Cui1<T> implements Bindable<T, ColumnUi> {

    private Cui1() {
    }

    public abstract ColumnUi bind(T condition);

    public Cui1<T> padBottom(final Sizelet padlet) {
        final Cui1<T> ui1 = this;
        return Cui1.create(new OnBind<T>() {
            @NonNull
            @Override
            public ColumnUi onBind(T condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).padBottom(padlet);
            }
        });
    }

    public Cui1<T> padHorizontal(final Sizelet padlet) {
        final Cui1<T> ui1 = this;
        return Cui1.create(new OnBind<T>() {
            @NonNull
            @Override
            public ColumnUi onBind(T condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).padHorizontal(padlet);
            }
        });
    }

    public Cui1<T> placeBefore(final ColumnUi columnUi, final int elevate) {
        final Cui1<T> ui1 = this;
        return Cui1.create(new OnBind<T>() {
            @NonNull
            @Override
            public ColumnUi onBind(T condition) {
                // TODO Think about using lifting operations.
                return ui1.bind(condition).placeBefore(columnUi, elevate);
            }
        });
    }

    public static <T> Cui1<T> create(final OnBind<T> onBind) {
        return new Cui1<T>() {
            @Override
            public ColumnUi bind(T condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<T> {
        @NonNull
        ColumnUi onBind(T condition);
    }
}

