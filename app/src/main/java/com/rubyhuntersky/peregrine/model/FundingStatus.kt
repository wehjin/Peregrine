package com.rubyhuntersky.peregrine.model

import java.math.BigDecimal
import java.math.BigDecimal.ZERO

/**
 * @author Jeffrey Yu
 * @since 6/4/16.
 */

data class FundingStatus(val buyIntention: BuyIntention, val cashAvailable: BigDecimal) {

    val requiredAmount: BigDecimal get() = buyIntention.amount
    val isFullyFunded: Boolean get() = cashAvailable.compareTo(requiredAmount) >= 0
    val fundedAmount: BigDecimal get() = if (isFullyFunded) requiredAmount else cashAvailable
    val shortfall: BigDecimal get() = if (isFullyFunded) ZERO else requiredAmount.subtract(cashAvailable)
    val assetPrice: BigDecimal get() = buyIntention.assetNamePrice.price
    val fundedShares: BigDecimal get() = fundedAmount.divide(assetPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP)
}