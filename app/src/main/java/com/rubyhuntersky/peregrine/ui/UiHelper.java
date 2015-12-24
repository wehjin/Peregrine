package com.rubyhuntersky.peregrine.ui;

import android.text.format.DateUtils;

import java.util.Date;

/**
 * @author wehjin
 * @since 12/24/15.
 */

public class UiHelper {

    public static CharSequence getRelativeTimeString(long time) {
        final long elapsed = new Date().getTime() - time;
        if (elapsed >= 0 && elapsed < 60000) {
            return "seconds ago";
        } else {
            return DateUtils.getRelativeTimeSpanString(time);
        }
    }
}
