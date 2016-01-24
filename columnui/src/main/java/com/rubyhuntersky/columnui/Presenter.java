package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface Presenter extends Observer, Presentation {

    Human getHuman();
    Column getColumn();
    void addPresentation(Presentation presentation);
}