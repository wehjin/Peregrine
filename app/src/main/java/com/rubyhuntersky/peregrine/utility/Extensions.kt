package com.rubyhuntersky.peregrine.utility

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.rubyhuntersky.peregrine.model.Group
import com.rubyhuntersky.peregrine.ui.UiHelper
import java.math.BigDecimal

/**
 * @author Jeffrey Yu
 * @since 2/15/17.
 */

fun BigDecimal.toCurrencyDisplayString(): String = UiHelper.getCurrencyDisplayString(this)
fun BigDecimal.toLabelAndCurrencyDisplayString(label: String): String = "$label ${toCurrencyDisplayString()}"
fun BigDecimal.toSharesDisplayString(): String = "${toIntString()} shares"
fun BigDecimal.toIntString(): String = setScale(0, BigDecimal.ROUND_HALF_DOWN).toPlainString()

val List<Group>.fullValue: BigDecimal
    get() = this.map { it.value!! }.reduce(BigDecimal::add)

fun Bundle.withParcelable(key: String, parcelable: Parcelable): Bundle {
    this.putParcelable(key, parcelable)
    return this
}

fun Fragment.withArguments(arguments: Bundle): Fragment {
    this.arguments = arguments
    return this
}
