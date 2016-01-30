package com.rubyhuntersky.columnui;

import com.rubyhuntersky.columnui.presenters.Presenter;

/**
 * @author wehjin
 * @since 1/23/16.
 */

public interface OnPresent<T> {
    void onPresent(Presenter<T> presenter);
}
