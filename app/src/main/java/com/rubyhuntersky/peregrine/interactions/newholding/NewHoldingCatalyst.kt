package com.rubyhuntersky.peregrine.interactions.newholding

import android.app.AlertDialog
import android.content.Context
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.data.Databook
import com.rubyhuntersky.peregrine.data.OfflineLot

class NewHoldingCatalyst(private val context: Context, private val databook: Databook) {

    fun startInteraction() {
        AlertDialog.Builder(context)
                .setTitle(R.string.new_holding)
                .setMessage("TSLA Stock, 1 share, $300 per share")
                .setPositiveButton(R.string.save) { _, _ ->
                    val newOfflineHolding = OfflineLot("TSLA", 1)
                    val newPortfolio = databook.portfolio.addOfflineHolding(newOfflineHolding)
                    databook.portfolio = newPortfolio
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .setCancelable(true)
                .show()
    }
}