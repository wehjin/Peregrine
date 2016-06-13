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
        val BigDecimal.intString: String get() = "${setScale(0, BigDecimal.ROUND_HALF_DOWN).toPlainString()}"
        val BigDecimal.shares: String get() = "${setScale(0, BigDecimal.ROUND_HALF_DOWN).toPlainString()} shares"

        fun getSharesString(shareCount: BigDecimal): String {
            return shareCount.shares
        }
    }
}
