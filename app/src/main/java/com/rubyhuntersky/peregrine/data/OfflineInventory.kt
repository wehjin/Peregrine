package com.rubyhuntersky.peregrine.data

class OfflineInventory(val lots: List<OfflineLot> = emptyList()) {

    fun addLot(offlineLot: OfflineLot): OfflineInventory {
        val newLots = lots.toMutableList().apply {
            add(offlineLot)
        }
        return OfflineInventory(newLots)
    }

    fun removeLot(position: Int): OfflineInventory {
        val newLots = lots.toMutableList().apply {
            removeAt(position)
        }
        return OfflineInventory(newLots)
    }
}