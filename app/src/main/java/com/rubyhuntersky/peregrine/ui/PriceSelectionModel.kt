package com.rubyhuntersky.peregrine.ui

import com.rubyhuntersky.peregrine.model.AssetNamePrice
import java.math.BigDecimal

data class PriceSelectionModel(val amount: BigDecimal, val namePrices: List<AssetNamePrice>, val selectedNamePrice: AssetNamePrice, val label: String)