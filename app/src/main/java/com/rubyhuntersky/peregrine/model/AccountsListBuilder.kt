package com.rubyhuntersky.peregrine.model

import org.json.JSONException
import java.util.*

internal class AccountsListBuilder : Storage.Builder<AllAccounts> {

    override fun buildFallback(): AllAccounts? {
        return AllAccounts(emptyList(), Date(0))
    }

    @Throws(JSONException::class)
    override fun build(jsonString: String): AllAccounts {
        return AllAccounts.fromJson(jsonString)
    }

    @Throws(JSONException::class)
    override fun stringify(`object`: AllAccounts): String {
        return AllAccounts.toJson(`object`)
    }
}
