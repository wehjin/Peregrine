package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation1;
import com.rubyhuntersky.columnui.ui.Ui1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tui1<C> implements Ui1<Tile, C> {

    private Tui1() {
    }

    @Override
    public abstract BoundTui1<C> bind(C condition);

    public Cui1<C> toColumn() {
        return Cui1.create(new Cui1.OnBind<C>() {
            @NonNull
            @Override
            public BoundCui1<C> onBind(final C condition) {
                return new BoundCui1<C>() {

                    @Override
                    public C getCondition() {
                        return condition;
                    }

                    @Override
                    public Presentation1<C> present(Human human, Column display, Observer observer) {
                        return Tui1.this.bind(condition).presentToColumn(human, display, observer);
                    }
                };
            }
        });
    }

    public static <C> Tui1<C> create(final OnBind<C> onBind) {
        return new Tui1<C>() {

            @Override
            public BoundTui1<C> bind(C condition) {
                return onBind.onBind(condition);
            }
        };
    }

    public interface OnBind<C> {
        @NonNull
        BoundTui1<C> onBind(C condition);
    }
}
