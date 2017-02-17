package com.rubyhuntersky.peregrine.model

import android.os.Parcelable

import java.math.BigDecimal

interface FundingOption : Parcelable {
    val assetName: String
    val sharesAvailableToSell: BigDecimal
    val sellPrice: BigDecimal
    val valueWhenSold: BigDecimal

    fun getSharesToRaiseFunds(funds: BigDecimal): BigDecimal {
        return if (sellPrice == BigDecimal.ZERO) {
            BigDecimal.ZERO
        } else {
            funds.divide(sellPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP)
                    .setScale(0, BigDecimal.ROUND_CEILING)
        }
    }
}
