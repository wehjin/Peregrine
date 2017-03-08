package com.rubyhuntersky.peregrine.model

import com.rubyhuntersky.peregrine.ui.UiHelper
import com.rubyhuntersky.peregrine.utility.getDate
import com.rubyhuntersky.peregrine.utility.getJSONObjectList
import com.rubyhuntersky.peregrine.utility.putDate
import com.rubyhuntersky.peregrine.utility.putJSONObjectList
import org.json.JSONObject
import java.util.*

/**
 * @author wehjin
 * *
 * @since 11/16/15.
 */

class AllAccounts(val accounts: List<EtradeAccount>, val arrivalDate: Date) {

    val relativeArrivalTime: CharSequence
        get() = UiHelper.getRelativeTimeString(arrivalDate.time)

    companion object {
        val JSONKEY_ACCOUNTS = "accounts"
        val JSONKEY_ARRIVAL_DATE = "arrivalDate"

        @JvmStatic
        fun fromJson(string: String): AllAccounts {
            val jsonObject = JSONObject(string)
            return AllAccounts(
                    accounts = jsonObject.getJSONObjectList(JSONKEY_ACCOUNTS).map(::EtradeAccount),
                    arrivalDate = jsonObject.getDate(JSONKEY_ARRIVAL_DATE)
            )
        }

        @JvmStatic
        fun toJson(allAccounts: AllAccounts): String {
            return allAccounts.toJSONObject().toString()
        }

        fun AllAccounts.toJSONObject(): JSONObject {
            val jsonObject = JSONObject()
            with(jsonObject) {
                putDate(JSONKEY_ARRIVAL_DATE, arrivalDate)
                putJSONObjectList(JSONKEY_ACCOUNTS, accounts.map { it.toJSONObject() })
            }
            return jsonObject
        }
    }
}
