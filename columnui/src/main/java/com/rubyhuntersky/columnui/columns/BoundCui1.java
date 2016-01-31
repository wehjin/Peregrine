package com.rubyhuntersky.columnui.columns;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.ui.BoundUi;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public abstract class BoundCui1 extends ColumnUi implements BoundUi<Column> {

    @Override
    public abstract Presentation present(Human human, Column column, Observer observer);

    public static <C> BoundCui1 create(final OnPresent1 onPresent1) {
        return new BoundCui1() {
            @Override
            public Presentation present(Human human, Column column, Observer observer) {
                return onPresent1.onPresent(human, column, observer);
            }

        };
    }

    public interface OnPresent1 {
        Presentation onPresent(Human human, Column column, Observer observer);
    }
}
