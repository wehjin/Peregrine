package com.rubyhuntersky.columnui;

import android.view.View;

/**
 * @author wehjin
 * @since 1/28/16.
 */
public class ViewPatch implements Patch {
    private UiView viewContainer;
    private final View view;

    public ViewPatch(UiView viewContainer, View view) {
        this.viewContainer = viewContainer;
        this.view = view;
    }

    @Override
    public void remove() {
        viewContainer.removeView(view);
    }
}
