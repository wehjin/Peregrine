package com.rubyhuntersky.peregrine.ui

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.rubyhuntersky.peregrine.model.AssetNamePrice
import com.rubyhuntersky.peregrine.model.Values
import com.rubyhuntersky.peregrine.utility.toLabelAndCurrencyDisplayString
import com.rubyhuntersky.peregrine.utility.toSharesDisplayString
import kotlinx.android.synthetic.main.cell_amount_price_shares.view.*
import java.math.BigDecimal

class PriceSelectionViewModel(val itemView: View) {
    fun bind(model: PriceSelectionModel, onPriceSelected: (AssetNamePrice, BigDecimal) -> Unit) {

        itemView.amountText.text = model.amount.toLabelAndCurrencyDisplayString(model.label)

        val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, model.namePrices.map { it.toDisplayString() })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemView.priceSpinner.adapter = adapter
        itemView.priceSpinner.setSelection(model.namePrices.indexOf(model.selectedNamePrice))
        itemView.priceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // View is null when rotating screen.
                if (view == null) {
                    return
                }
                val price = model.namePrices[position]
                val shares = model.amount.divide(price.price, Values.SCALE, BigDecimal.ROUND_HALF_UP)
                itemView.sharesText.text = shares.toSharesDisplayString()
                onPriceSelected(price, shares)
                itemView.priceSpinner.requestLayout()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

}