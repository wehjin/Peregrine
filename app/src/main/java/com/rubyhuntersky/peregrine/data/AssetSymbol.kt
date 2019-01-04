package com.rubyhuntersky.peregrine.data

class AssetSymbol(string: String) {

    val value = string.toUpperCase()

    override fun toString(): String = value
}
