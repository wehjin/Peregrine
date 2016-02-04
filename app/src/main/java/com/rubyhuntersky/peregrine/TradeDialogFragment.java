package com.rubyhuntersky.peregrine;

import android.support.v7.app.AppCompatDialogFragment;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 2/4/16.
 */

public class TradeDialogFragment extends AppCompatDialogFragment {

    public static String getSharesString(BigDecimal shareCount) {
        final String shareCountString = shareCount.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString();
        return String.format("%s shares", shareCountString);
    }
}
