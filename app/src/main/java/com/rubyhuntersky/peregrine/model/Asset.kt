package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import com.rubyhuntersky.peregrine.utility.DefaultParcelable
import com.rubyhuntersky.peregrine.utility.DefaultParcelable.Companion.generateCreator
import com.rubyhuntersky.peregrine.utility.read
import com.rubyhuntersky.peregrine.utility.write
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*

class Asset(
        var symbol: String = "",
        var typeCode: String = "",
        var description: String = "",
        var direction: String = "",
        var quantity: BigDecimal = BigDecimal.ZERO,
        var currentPrice: BigDecimal = BigDecimal.ZERO,
        var marketValue: BigDecimal = BigDecimal.ZERO,
        var purchaseValue: BigDecimal = BigDecimal.ZERO,
        var accountId: String = "",
        var accountDescription: String = "",
        var arrivalTime: Date = Date(Long.MAX_VALUE)
) {
    fun isInGroup(group: Group, assignments: Assignments): Boolean {
        val partitionId = assignments.getPartitionId(symbol)
        return partitionId != null && partitionId == group.partitionId
    }

    fun toFundingOption(): FundingOption = AssetFundingOption(this)

    constructor(accountId: String, symbol: String, marketValue: BigDecimal, shares: BigDecimal, currentPrice: BigDecimal) : this() {
        this.accountId = accountId
        this.symbol = symbol
        this.marketValue = marketValue
        this.quantity = shares
        this.currentPrice = currentPrice
    }

    @Throws(JSONException::class)
    constructor(jsonObject: JSONObject, accountId: String, accountDescription: String, arrivalTime: Date) : this() {
        val productId = jsonObject.getJSONObject("productId")
        symbol = productId.getString("symbol")
        typeCode = productId.getString("typeCode")
        description = jsonObject.getString("description")
        direction = jsonObject.getString("longOrShort")
        quantity = BigDecimal(jsonObject.getLong("qty"))
        currentPrice = BigDecimal(jsonObject.getString("currentPrice"))
        marketValue = BigDecimal(jsonObject.getString("marketValue"))
        purchaseValue = BigDecimal(jsonObject.getString("costBasis"))
        this.accountId = accountId
        this.accountDescription = accountDescription
        this.arrivalTime = arrivalTime
    }

    private data class AssetFundingOption(override val assetName: String,
                                          override val sharesAvailableToSell: BigDecimal,
                                          override val sellPrice: BigDecimal) : FundingOption, DefaultParcelable {

        override val valueWhenSold: BigDecimal get() = sharesAvailableToSell.multiply(sellPrice)

        constructor(asset: Asset) : this(asset.symbol, asset.quantity, asset.currentPrice)

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.write(assetName, sharesAvailableToSell, sellPrice)
        }

        companion object {
            @JvmStatic val CREATOR = generateCreator { AssetFundingOption(it.read(), it.read(), it.read()) }
        }
    }
}
