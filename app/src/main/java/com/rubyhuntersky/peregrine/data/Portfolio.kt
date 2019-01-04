package com.rubyhuntersky.peregrine.data

data class Portfolio(val offlineInventory: OfflineInventory = OfflineInventory()) {

    fun isEmpty(): Boolean =
            offlineInventory.lots.isEmpty()

    fun addOfflineHolding(offlineLot: OfflineLot): Portfolio {

        val newOfflineInventory = offlineInventory.addHolding(offlineLot)
        return Portfolio(newOfflineInventory)
    }
}