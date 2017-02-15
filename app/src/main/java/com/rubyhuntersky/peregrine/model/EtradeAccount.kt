package com.rubyhuntersky.peregrine.model

import com.rubyhuntersky.peregrine.utility.getChildElement
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Element
import java.math.BigDecimal

data class EtradeAccount(val accountId: String, val description: String, val registrationType: String, val netAccountValue: BigDecimal) {

    constructor(accountElement: Element) : this(
            accountId = accountElement.getChildElement("accountId").textContent,
            description = accountElement.getChildElement("accountDesc").textContent,
            registrationType = accountElement.getChildElement("registrationType").textContent,
            netAccountValue = BigDecimal(accountElement.getChildElement("netAccountValue").textContent)
    )

    @Throws(JSONException::class)
    constructor(jsonObject: JSONObject) : this(
            description = jsonObject.getString(JSONKEY_DESCRIPTION),
            accountId = jsonObject.getString(JSONKEY_ACCOUNT_ID),
            registrationType = jsonObject.getString(JSONKEY_REGISTRATION_TYPE),
            netAccountValue = BigDecimal(jsonObject.getString(JSONKEY_NET_ACCOUNT_VALUE))
    )

    @Throws(JSONException::class)
    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(JSONKEY_DESCRIPTION, this.description)
        jsonObject.put(JSONKEY_ACCOUNT_ID, this.accountId)
        jsonObject.put(JSONKEY_REGISTRATION_TYPE, this.registrationType)
        jsonObject.put(JSONKEY_NET_ACCOUNT_VALUE, this.netAccountValue.toString())
        return jsonObject
    }

    companion object {
        private val JSONKEY_DESCRIPTION = "description"
        private val JSONKEY_ACCOUNT_ID = "accountId"
        private val JSONKEY_REGISTRATION_TYPE = "registrationType"
        private val JSONKEY_NET_ACCOUNT_VALUE = "netAccountValue"
    }
}
