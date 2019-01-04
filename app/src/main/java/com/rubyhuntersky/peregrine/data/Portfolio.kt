package com.rubyhuntersky.peregrine.data

data class Portfolio(val offlineInventory: OfflineInventory = OfflineInventory()) {

    fun isEmpty(): Boolean =
            offlineInventory.holdings.isEmpty()

    fun addOfflineHolding(offlineHolding: OfflineHolding): Portfolio {

        val newOfflineInventory = offlineInventory.addHolding(offlineHolding)
        return Portfolio(newOfflineInventory)
    }
}