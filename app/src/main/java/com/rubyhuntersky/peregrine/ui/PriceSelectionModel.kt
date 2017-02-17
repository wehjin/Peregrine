package com.rubyhuntersky.peregrine.ui

import com.rubyhuntersky.peregrine.model.AssetPrice
import java.math.BigDecimal

data class PriceSelectionModel(val amount: BigDecimal, val prices: List<AssetPrice>, val selectedPrice: AssetPrice, val label: String)