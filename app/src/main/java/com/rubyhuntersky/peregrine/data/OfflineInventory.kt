package com.rubyhuntersky.peregrine.data

class OfflineInventory(val holdings: List<OfflineHolding> = emptyList()) {

    fun addHolding(offlineHolding: OfflineHolding): OfflineInventory {
        val newHoldings = holdings.toMutableList().apply {
            add(offlineHolding)
        }
        return OfflineInventory(newHoldings)
    }
}