package com.rubyhuntersky.peregrine.data

data class OfflineHolding(val symbol: HoldingSymbol, val shareCount: Long, val sharePrice: Double? = null) {

    constructor(string: String, shareCount: Long, sharePrice: Double? = null) : this(HoldingSymbol(string), shareCount, sharePrice)
}