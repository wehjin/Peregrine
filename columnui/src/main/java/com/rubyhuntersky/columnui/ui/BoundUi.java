package com.rubyhuntersky.columnui.ui;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

public interface BoundUi<T> extends Ui<T> {

    @Override
    Presentation present(Human human, T display, Observer observer);
}
