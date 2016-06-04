package com.rubyhuntersky.peregrine.ui

import android.support.v7.app.AppCompatDialogFragment

import java.math.BigDecimal

/**
 * @author wehjin
 * *
 * @since 2/4/16.
 */

open class TradeDialogFragment : AppCompatDialogFragment() {
    companion object {

        fun getSharesString(shareCount: BigDecimal): String {
            val shareCountString = shareCount.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString()
            return "$shareCountString shares"
        }
    }
}
