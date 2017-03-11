package com.rubyhuntersky.peregrine.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AssetNamePrice
import com.rubyhuntersky.peregrine.model.GroupSaleOption
import com.rubyhuntersky.peregrine.utility.withArguments
import rx.Subscription
import rx.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.*

class SellDialogFragment : BottomSheetDialogFragment() {

    val TAG = SellDialogFragment::javaClass.name

    private val amount by lazy { arguments.getSerializable(AMOUNT_KEY) as BigDecimal }
    private val options by lazy { arguments.getParcelableArrayList<GroupSaleOption>(OPTIONS_KEY) }
    private val viewModel by lazy { PriceSelectionViewModel(view!!) }

    private val indexSubject = BehaviorSubject.create(0)
    private var indexSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = savedInstanceState ?: arguments
        indexSubject.onNext(data.getInt(SELECTED_INDEX_KEY))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_INDEX_KEY, indexSubject.value)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val assetPrices = options.map { AssetNamePrice(it.assetName, it.assetPrice) }

        indexSubject.subscribe { index ->
            val model = PriceSelectionModel(amount, assetPrices, assetPrices[index], "Sell")
            viewModel.bind(model, { _, _ -> })
            Log.i(TAG, "Account options: ${options[index].accountSaleOptions}")
        }
    }

    override fun onDestroyView() {
        indexSubscription?.unsubscribe()
        super.onDestroyView()
    }


    companion object {

        private val AMOUNT_KEY = "amountKey"
        private val OPTIONS_KEY = "optionsKey"
        private val SELECTED_INDEX_KEY = "selectedOptionIndexKey"

        @JvmStatic
        fun create(cashGoal: BigDecimal,
                   options: List<GroupSaleOption>,
                   initialOptionIndex: Int): SellDialogFragment {

            return SellDialogFragment().withArguments {
                putSerializable(AMOUNT_KEY, cashGoal)
                putParcelableArrayList(OPTIONS_KEY, ArrayList(options))
                putInt(SELECTED_INDEX_KEY, initialOptionIndex)
            }
        }
    }
}
