package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AssetPrice
import com.rubyhuntersky.peregrine.model.BuyProgram
import com.rubyhuntersky.peregrine.model.FundingAccount
import com.rubyhuntersky.peregrine.utility.withArguments
import com.rubyhuntersky.peregrine.utility.withParcelable
import java.math.BigDecimal


class BuyDialogFragment : BottomSheetDialogFragment() {

    lateinit var buyModel: BuyProgram

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inState = savedInstanceState ?: arguments
        buyModel = inState.getParcelable<BuyProgram>(PROGRAM_KEY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PROGRAM_KEY, buyModel)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = createContentView()
        dialog.setContentView(contentView)

        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun createContentView(): View {
        val view = View.inflate(context, R.layout.fragment_buy, null)

        val priceSelectionModel = PriceSelectionModel(
                amount = buyModel.budget,
                prices = buyModel.products,
                selectedPrice = buyModel.product,
                label = "Buy"
        )
        PriceSelectionViewModel(view).bind(priceSelectionModel) { price, shares -> }
        return view
    }

    companion object {

        val TAG: String = BuyDialogFragment::class.java.simpleName
        val PROGRAM_KEY = "programKey"

        @JvmStatic
        fun create(amount: BigDecimal, prices: List<AssetPrice>, selectedPrice: Int, fundingAccounts: List<FundingAccount>): BuyDialogFragment {
            val arguments = Bundle().withParcelable(PROGRAM_KEY, BuyProgram(amount, prices, selectedPrice, fundingAccounts))
            return BuyDialogFragment().withArguments(arguments)
        }
    }
}
