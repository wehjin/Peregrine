package com.rubyhuntersky.peregrine.utility

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.rubyhuntersky.peregrine.model.Group
import com.rubyhuntersky.peregrine.ui.UiHelper
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*

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

inline fun <reified T : Fragment> T.withArguments(arguments: Bundle): T {
    this.arguments = arguments
    return this
}

inline fun <reified T : Fragment> T.withArguments(init: Bundle.() -> Unit): T {
    arguments = Bundle().apply(init)
    return this
}

fun JSONArray.toJSONObjectList(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }

fun JSONObject.getJSONObjectList(fieldName: String): List<JSONObject> {
    return getJSONArray(fieldName).toJSONObjectList()
}

fun JSONObject.putJSONObjectList(fieldName: String, jsonObjectList: List<JSONObject>) {
    val jsonArray = jsonObjectList.fold(JSONArray(), JSONArray::put)
    put(fieldName, jsonArray)
}


fun JSONObject.getDate(fieldName: String): Date {
    return Date(getLong(fieldName))
}

fun JSONObject.putDate(fieldName: String, date: Date) {
    put(fieldName, date.time)
}
