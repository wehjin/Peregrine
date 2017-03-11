package com.rubyhuntersky.peregrine.model

import java.math.BigDecimal

/**
 * @author Jeffrey Yu
 * @since 6/4/16.
 */

data class BuyIntention(val amount: BigDecimal, val assetNamePrice: AssetNamePrice)
