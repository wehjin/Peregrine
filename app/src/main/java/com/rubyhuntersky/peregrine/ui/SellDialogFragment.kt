package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AssetPrice
import com.rubyhuntersky.peregrine.model.Values
import com.rubyhuntersky.peregrine.utility.toLabelAndCurrencyDisplayString
import com.rubyhuntersky.peregrine.utility.toSharesDisplayString
import kotlinx.android.synthetic.main.fragment_sell.*
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.*

/**
 * @author wehjin
 * *
 * @since 1/19/16.
 */
class SellDialogFragment : TradeDialogFragment() {
    private val amount: BigDecimal by lazy { arguments.getSerializable(AMOUNT_KEY) as BigDecimal }
    private val assetPriceLists = BehaviorSubject.create(emptyList<AssetPrice>())
    private val selectedPrices = BehaviorSubject.create(AssetPrice("-", BigDecimal.ZERO))
    private var assetPriceListsSubscription: Subscription? = null
    private var selectedPricesSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        val data = savedInstanceState ?: arguments
        assetPriceLists.onNext(data.getParcelableArrayList<AssetPrice>(PRICES_KEY))
        selectedPrices.onNext(data.getParcelable<AssetPrice>(SELECTED_PRICE_KEY))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(PRICES_KEY, ArrayList(assetPriceLists.value))
        outState.putParcelable(SELECTED_PRICE_KEY, selectedPrices.value)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        amountText.text = amount.toLabelAndCurrencyDisplayString("Sell")
        assetPriceListsSubscription = assetPriceLists.subscribe {
            AssetPriceSelectionViewModel().bind(it) { assetPrice -> selectedPrices.onNext(assetPrice) }
        }
        selectedPricesSubscription = selectedPrices.subscribe { price ->
            val sharesToSell = amount.divide(price.price, Values.SCALE, BigDecimal.ROUND_HALF_UP)
            sharesText.text = sharesToSell.toSharesDisplayString()
        }
    }

    override fun onDestroyView() {
        assetPriceListsSubscription?.unsubscribe()
        selectedPricesSubscription?.unsubscribe()
        super.onDestroyView()
    }

    fun AssetPrice.toDisplayString(): String = price.toLabelAndCurrencyDisplayString(name)

    inner class AssetPriceSelectionViewModel {
        fun bind(assetPriceList: List<AssetPrice>, onAssetPriceSelected: (AssetPrice) -> Unit) {

            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, assetPriceList.map { it.toDisplayString() })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            priceSpinner.adapter = adapter
            priceSpinner.setSelection(assetPriceList.indexOf(selectedPrices.value))
            priceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    onAssetPriceSelected(assetPriceList[position])
                    priceSpinner.requestLayout()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
        }
    }


    companion object {

        val AMOUNT_KEY = "amountKey"
        val PRICES_KEY = "pricesKey"
        val SELECTED_PRICE_KEY = "selectedPriceKey"

        fun create(amount: BigDecimal, priceOptions: List<AssetPrice>, selectedPrice: AssetPrice): SellDialogFragment {
            val fragment = SellDialogFragment()
            fragment.arguments = newArguments(amount, priceOptions, selectedPrice)
            return fragment
        }

        private fun newArguments(amount: BigDecimal, priceOptions: List<AssetPrice>, selectedPrice: AssetPrice): Bundle {
            val arguments = Bundle()
            arguments.putSerializable(AMOUNT_KEY, amount)
            arguments.putParcelableArrayList(PRICES_KEY, ArrayList(priceOptions))
            arguments.putParcelable(SELECTED_PRICE_KEY, selectedPrice)
            return arguments
        }
    }
}
