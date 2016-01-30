package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.conditions.Human;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.presenters.BasePresenter;
import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 1/27/16.
 */

abstract public class BaseUi<T> {
    abstract public Presentation present(Human human, T display, Observer observer);

    public static <T> BaseUi<T> create(final OnPresent<T> onPresent, final PresenterAdapter<T> presenterAdapter) {
        return new BaseUi<T>() {
            @Override
            public Presentation present(final Human human, final T display, final Observer observer) {
                final Presenter<T> presenter = new BasePresenter<T>(human, display, observer) {
                    @Override
                    public float getWidth() {
                        return presenterAdapter.getWidth(this);
                    }

                    @Override
                    public float getHeight() {
                        return presenterAdapter.getHeight(this);
                    }
                };
                onPresent.onPresent(presenter);
                return presenter;
            }
        };
    }

    public interface PresenterAdapter<T> {
        float getWidth(Presenter<T> presenter);
        float getHeight(Presenter<T> presenter);
    }
}
