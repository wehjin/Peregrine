package com.rubyhuntersky.peregrine.data

data class Portfolio(val offlineInventory: OfflineInventory = OfflineInventory()) {

    fun isEmpty(): Boolean =
            offlineInventory.lots.isEmpty()

    fun addOfflineLot(offlineLot: OfflineLot): Portfolio {

        val newOfflineInventory = offlineInventory.addLot(offlineLot)
        return Portfolio(newOfflineInventory)
    }

    fun removeOfflineLot(position: Int): Portfolio {
        val newOfflineInventory = offlineInventory.removeLot(position)
        return Portfolio(newOfflineInventory)
    }
}