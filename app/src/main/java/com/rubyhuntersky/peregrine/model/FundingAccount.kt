package com.rubyhuntersky.peregrine.model

import android.os.Parcelable
import com.rubyhuntersky.peregrine.utility.toIntString
import java.math.BigDecimal

interface FundingAccount : Parcelable {
    val accountName: String
    val cashAvailable: BigDecimal
    fun getFundingOptions(exclude: String): List<FundingOption>
    fun hasFundsForBuyIntention(buyIntention: BuyIntention): Boolean
    fun getStatusForBuy(buyIntention: BuyIntention): FundingStatus

    fun hasFundsForPurchase(purchaseAmount: BigDecimal): Boolean {
        return cashAvailable >= purchaseAmount
    }

    fun getSharesPurchasableWithCash(product: AssetNamePrice): String {
        return cashAvailable.divide(product.price, Values.SCALE, BigDecimal.ROUND_HALF_UP).toIntString()
    }

    fun isFullyFundedForPurchase(purchaseAmount: BigDecimal): Boolean {
        return getShortfall(purchaseAmount) <= BigDecimal.ZERO
    }

    fun getShortfall(purchaseAmount: BigDecimal): BigDecimal {
        return purchaseAmount.subtract(cashAvailable)
    }
}
