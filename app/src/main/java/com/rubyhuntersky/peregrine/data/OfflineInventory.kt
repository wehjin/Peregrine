package com.rubyhuntersky.peregrine.data

class OfflineInventory(val lots: List<OfflineLot> = emptyList()) {

    fun addHolding(offlineLot: OfflineLot): OfflineInventory {
        val newHoldings = lots.toMutableList().apply {
            add(offlineLot)
        }
        return OfflineInventory(newHoldings)
    }
}