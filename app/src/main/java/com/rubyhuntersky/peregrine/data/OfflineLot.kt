package com.rubyhuntersky.peregrine.data

data class OfflineLot(val symbol: AssetSymbol, val shareCount: Long, val sharePrice: Double? = null) {

    constructor(string: String, shareCount: Long, sharePrice: Double? = null) : this(AssetSymbol(string), shareCount, sharePrice)
}