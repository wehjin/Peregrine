package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.bars.Bar;
import com.rubyhuntersky.columnui.conditions.Human;

/**
 * @author wehjin
 * @since 1/27/16.
 */

abstract public class BarUi extends BaseUi<Bar> {

    @Override
    abstract public Presentation present(Human human, Bar bar, Observer observer);

    public static BarUi create(final OnPresent<Bar> onPresent) {
        return new BarUi() {
            @Override
            public Presentation present(Human human, final Bar bar, Observer observer) {
                final BasePresenter<Bar> presenter = new BasePresenter<Bar>(human, bar, observer) {
                    @Override
                    public float getWidth() {
                        float union = 0;
                        for (Presentation presentation : presentations) {
                            union = Math.max(union, presentation.getWidth());
                        }
                        return union;
                    }

                    @Override
                    public float getHeight() {
                        return display.fixedHeight;
                    }
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }
}

