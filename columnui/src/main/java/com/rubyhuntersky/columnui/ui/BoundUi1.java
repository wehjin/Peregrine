package com.rubyhuntersky.columnui.ui;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation1;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public interface BoundUi1<T, C> extends Ui<T> {

    C getCondition();

    @Override
    Presentation1<C> present(Human human, T display, Observer observer);
}
