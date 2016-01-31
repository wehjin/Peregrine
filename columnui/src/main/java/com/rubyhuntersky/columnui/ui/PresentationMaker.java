package com.rubyhuntersky.columnui.ui;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;

/**
 * @author wehjin
 * @since 1/30/16.
 */

public interface PresentationMaker<P extends Presentation, D> {
    P present(Human human, D display, Observer observer, int index);
    P resize(float width, float height, P basis);
}
