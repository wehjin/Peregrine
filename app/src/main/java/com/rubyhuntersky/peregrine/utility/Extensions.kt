package com.rubyhuntersky.peregrine.utility

import com.rubyhuntersky.peregrine.ui.TradeDialogFragment
import com.rubyhuntersky.peregrine.ui.UiHelper
import java.math.BigDecimal

/**
 * @author Jeffrey Yu
 * @since 2/15/17.
 */

fun BigDecimal.toCurrencyDisplayString(): String = UiHelper.getCurrencyDisplayString(this)

fun BigDecimal.toLabelAndCurrencyDisplayString(label: String): String {
    return "$label ${toCurrencyDisplayString()}"
}

fun BigDecimal.toSharesDisplayString(): String {
    return TradeDialogFragment.getSharesString(this)
}
