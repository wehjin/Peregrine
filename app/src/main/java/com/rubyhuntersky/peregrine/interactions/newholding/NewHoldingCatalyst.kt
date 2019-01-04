package com.rubyhuntersky.peregrine.interactions.newholding

import android.app.AlertDialog
import android.content.Context
import com.rubyhuntersky.peregrine.R

class NewHoldingCatalyst(private val context: Context) {

    fun startInteraction() {
        AlertDialog.Builder(context)
                .setTitle(R.string.new_holding)
                .setMessage("TSLA Stock, 1 share, $300 per share")
                .setPositiveButton(R.string.save) { _, _ ->
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .setCancelable(true)
                .show()
    }
}