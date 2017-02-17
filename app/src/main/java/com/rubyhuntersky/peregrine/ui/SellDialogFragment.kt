package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AssetPrice
import com.rubyhuntersky.peregrine.ui.PriceSelectionModel
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.*

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
        assetPriceListsSubscription = assetPriceLists.subscribe {
            PriceSelectionViewModel(view!!).bind(PriceSelectionModel(amount, it, selectedPrices.value, "Sell")) { assetPrice, shares -> }
        }
    }

    override fun onDestroyView() {
        assetPriceListsSubscription?.unsubscribe()
        selectedPricesSubscription?.unsubscribe()
        super.onDestroyView()
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
