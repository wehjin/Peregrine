package com.rubyhuntersky.peregrine.ui;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

/**
 * @author wehjin
 * @since 12/24/15.
 */

public class UiHelper {

    public static final NumberFormat CURRENCY_INSTANCE = NumberFormat.getCurrencyInstance();

    public static CharSequence getRelativeTimeString(long time) {
        final long elapsed = new Date().getTime() - time;
        if (elapsed >= 0 && elapsed < 60000) {
            return "seconds ago";
        } else {
            return DateUtils.getRelativeTimeSpanString(time);
        }
    }

    @NonNull
    public static String getCurrencyDisplayString(BigDecimal value) {
        return CURRENCY_INSTANCE.format(value);
    }
}
