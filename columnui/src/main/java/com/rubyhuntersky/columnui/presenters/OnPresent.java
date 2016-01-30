package com.rubyhuntersky.columnui.presenters;

import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface OnPresent<T> {
    void onPresent(Presenter<T> presenter);
}
