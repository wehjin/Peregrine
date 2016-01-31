package com.rubyhuntersky.columnui.tiles;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.columns.Column;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation1;
import com.rubyhuntersky.columnui.ui.BoundUi1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class BoundCui1<C> extends ColumnUi implements BoundUi1<Column, C> {

    @Override
    public abstract Presentation1<C> present(Human human, Column column, Observer observer);

    public static <C> BoundCui1<C> create(final C condition, final OnPresent1<C> onPresent1) {
        return new BoundCui1<C>() {
            @Override
            public Presentation1<C> present(Human human, Column column, Observer observer) {
                return onPresent1.onPresent(human, column, observer);
            }

            @Override
            public C getCondition() {
                return condition;
            }
        };
    }

    public interface OnPresent1<C> {
        Presentation1<C> onPresent(Human human, Column column, Observer observer);
    }
}
