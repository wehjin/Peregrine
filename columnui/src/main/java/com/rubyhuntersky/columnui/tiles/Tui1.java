package com.rubyhuntersky.columnui.tiles;

import android.support.annotation.NonNull;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/30/16.
 */

abstract public class Tui1<T> implements Bindable<T> {

    private Tui1() {
    }

    public abstract void bind(T condition);

    public abstract void present(Human human, Tile tile, Observer observer);

    public static <T> Tui1<T> create(final OnBind<T> onBind) {
        return new Tui1<T>() {

            private Human human;
            private Tile tile;
            private Observer observer;
            private OnPresentation onPresentation;
            private TileUi tileUi;

            @Override
            public void present(Human human, Tile tile, Observer observer) {

                this.human = human;
                this.tile = tile;
                this.observer = observer;
                this.onPresentation = onPresentation;
                if (tileUi == null) {
                    return;
                }

            }

            @Override
            public void bind(T condition) {
                tileUi = onBind.bind(condition);
                tileUi.present(human, tile, observer);
            }
        };
    }

    public interface OnBind<T> {
        @NonNull
        TileUi bind(T condition);
    }

    public interface OnPresentation {
    }

}

