package com.rubyhuntersky.peregrine.data

class HoldingSymbol(string: String) {

    val value = string.toUpperCase()

    override fun toString(): String = value
}
