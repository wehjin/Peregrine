package com.rubyhuntersky.columnui.reactions;

import android.support.annotation.Nullable;

import com.rubyhuntersky.columnui.Reaction;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public class ItemSelectionReaction<T> extends Reaction {
    private final T item;

    public ItemSelectionReaction(@Nullable T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
