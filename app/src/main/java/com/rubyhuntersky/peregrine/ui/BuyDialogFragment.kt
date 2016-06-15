package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rubyhuntersky.coloret.Coloret.BLACK
import com.rubyhuntersky.gx.Gx.colorColumn
import com.rubyhuntersky.gx.Gx.dropDownMenuDiv
import com.rubyhuntersky.gx.Gx.gapColumn
import com.rubyhuntersky.gx.Gx.textColumn
import com.rubyhuntersky.gx.android.AndroidHuman
import com.rubyhuntersky.gx.android.FrameLayoutScreen
import com.rubyhuntersky.gx.basics.Sizelet
import com.rubyhuntersky.gx.basics.Sizelet.readables
import com.rubyhuntersky.gx.basics.TextStylet.IMPORTANT_DARK
import com.rubyhuntersky.gx.basics.TextStylet.READABLE_DARK
import com.rubyhuntersky.gx.devices.poles.Pole
import com.rubyhuntersky.gx.reactions.ItemSelectionReaction
import com.rubyhuntersky.gx.reactions.Reaction
import com.rubyhuntersky.gx.uis.divs.Div
import com.rubyhuntersky.gx.uis.divs.Div0
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.*
import com.rubyhuntersky.peregrine.ui.UiHelper.getCurrencyDisplayString
import java.math.BigDecimal

/**
 * @author wehjin
 * *
 * @since 1/19/16.
 */
class BuyDialogFragment() : TradeDialogFragment() {

    companion object {

        val DIVISION_SIGN = "\u00f7"
        val DIVIDER = colorColumn(readables(.25f), BLACK)
        val TAG = BuyDialogFragment::class.java.simpleName
        val PROGRAM_KEY = "programKey"

        fun create(amount: BigDecimal, prices: List<AssetPrice>, selectedPrice: Int, fundingAccounts: List<FundingAccount>): BuyDialogFragment {
            val buyProgram = BuyProgram(amount, prices, selectedPrice, fundingAccounts)

            val arguments = Bundle()
            arguments.putParcelable(PROGRAM_KEY, buyProgram)

            val fragment = BuyDialogFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private var presentation: Div.Presentation? = null
    private var frameLayout: FrameLayout? = null
    private var ui: Div0? = null
    var frameWidth = 576
    var frameHeight = 1070
    lateinit var purchase: BuyProgram

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PROGRAM_KEY, purchase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        purchase = arguments.getParcelable<BuyProgram>(PROGRAM_KEY)

        val windowSize = Point()
        activity.windowManager.defaultDisplay.getSize(windowSize)
        val portion = .9
        frameWidth = (windowSize.x * portion).toInt()
        frameHeight = (windowSize.y * portion).toInt()

        ui = Div0.create(object : Div.OnPresent {
            override fun onPresent(presenter: Div.Presenter) {
                presenter.addPresentation(object : Div.PresenterPresentation(presenter) {

                    var presentation: Div.Presentation? = null

                    val productMenu = "productMenu"
                    val accountMenu = "accountMenu"
                    val assetMenu = "assetMenu"
                    val divider = DIVIDER.padHorizontal(Sizelet.HALF_FINGER)
                    val BigDecimal.currency: String get() = getCurrencyDisplayString(this)
                    val AssetPrice.divisor: String get() = "$DIVISION_SIGN $name ${price.currency}"
                    val AssetPrice.menuItem: Div0 get() = textColumn(divisor, IMPORTANT_DARK)
                    val FundingOption.menuItem: Div0 get() = textColumn("$DIVISION_SIGN $assetName ${sellPrice.currency}", IMPORTANT_DARK)

                    fun FundingAccount.hasFundsForPurchase(purchaseAmount: BigDecimal): Boolean {
                        return cashAvailable.compareTo(purchaseAmount) >= 0
                    }

                    fun FundingAccount.getSharesPurchasableWithCash(product: AssetPrice): String {
                        return cashAvailable.divide(product.price, Values.SCALE, BigDecimal.ROUND_HALF_UP).intString
                    }

                    fun FundingAccount.getShortfall(purchaseAmount: BigDecimal): BigDecimal {
                        return purchaseAmount.subtract(cashAvailable)
                    }

                    fun FundingAccount.isFullyFundedForPurchase(purchaseAmount: BigDecimal): Boolean {
                        return getShortfall(purchaseAmount).compareTo(BigDecimal.ZERO) <= 0
                    }

                    fun FundingAccount.getMenuItem(budget: BigDecimal, product: AssetPrice): Div0 {
                        return textColumn("Account $accountName", IMPORTANT_DARK)
                                .expandDown(gapColumn(Sizelet.READABLE))
                                .expandDown(if (hasFundsForPurchase(budget)) {
                                    textColumn("Sufficient funds ${cashAvailable.currency}", READABLE_DARK)
                                } else {
                                    textColumn("Buy ${getSharesPurchasableWithCash(product)} shares", READABLE_DARK)
                                            .expandDown(gapColumn(Sizelet.READABLE))
                                            .expandDown(textColumn("or", READABLE_DARK))
                                            .expandDown(gapColumn(Sizelet.IMPORTANT))
                                            .expandDown(textColumn("Add funds ${getShortfall(budget).currency}", IMPORTANT_DARK))
                                })
                    }

                    fun FundingOption.getSharesToRaiseFunds(funds: BigDecimal): BigDecimal {
                        if (sellPrice.equals(BigDecimal.ZERO)) {
                            return BigDecimal.ZERO
                        } else {
                            return funds.divide(sellPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP).setScale(0, BigDecimal.ROUND_CEILING)
                        }
                    }

                    private val div0: Div0
                        get() {

                            val budget = purchase.budget
                            val budgetLine = textColumn("Buy " + budget.currency, IMPORTANT_DARK)
                            val productSelector = dropDownMenuDiv(purchase.productIndex, purchase.products.map { it.menuItem }, productMenu)
                            val sharesInBudget = divider
                                    .expandDown(gapColumn(Sizelet.READABLE))
                                    .expandDown(textColumn("${purchase.sharesInBudget.intString} shares", IMPORTANT_DARK))
                            val productDecision = budgetLine.expandDown(productSelector).expandDown(sharesInBudget)
                            val accountDecision = if (purchase.accounts.isEmpty()) {
                                Div0.EMPTY
                            } else {
                                val product = purchase.product
                                dropDownMenuDiv(purchase.accountIndex, purchase.accounts.map { it.getMenuItem(budget, product) }, accountMenu)
                            }
                            val assetDecision = if (purchase.account?.isFullyFundedForPurchase(budget) ?: false || purchase.assets.isEmpty()) {
                                Div0.EMPTY
                            } else {
                                val shortfall = purchase.account!!.getShortfall(budget)
                                val asset = purchase.asset!!
                                dropDownMenuDiv(purchase.assetIndex, purchase.assets.map { it.menuItem }, assetMenu)
                                        .expandDown(divider)
                                        .expandDown(gapColumn(Sizelet.READABLE))
                                        .expandDown(textColumn("Sell ${asset.getSharesToRaiseFunds(shortfall)} shares", IMPORTANT_DARK))
                            }

                            return productDecision
                                    .expandDown(gapColumn(Sizelet.HALF_FINGER))
                                    .expandDown(accountDecision)
                                    .expandDown(assetDecision)
                                    .expandVertical(Sizelet.HALF_FINGER)
                        }

                    init {
                        present()
                    }

                    fun present() {
                        presentation?.cancel()
                        presentation = div0.present(human, pole, object : Div.ForwardingObserver(presenter) {
                            override fun onReaction(reaction: Reaction) {
                                when (reaction.source) {
                                    productMenu -> {
                                        val index = (reaction as ItemSelectionReaction<*>).item as Int
                                        purchase = purchase.withProductIndex(index)
                                        present()
                                    }
                                    accountMenu -> {
                                        val index = (reaction as ItemSelectionReaction<*>).item as Int
                                        purchase = purchase.withAccountIndex(index)
                                        present()
                                    }
                                    assetMenu -> {
                                        val index = (reaction as ItemSelectionReaction<*>).item as Int
                                        purchase = purchase.withAssetIndex(index)
                                        present()
                                    }
                                    else -> {
                                        super.onReaction(reaction)
                                    }
                                }
                            }
                        })

                    }

                    override fun onCancel() {
                        presentation?.cancel()
                    }
                })
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(tag, "onCreateView")

        val mainFrame = inflater!!.inflate(R.layout.fragment_buy, container, false) as FrameLayout
        frameLayout = object : FrameLayout(activity) {
            override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                super.onLayout(changed, left, top, right, bottom)
                Log.d(this@BuyDialogFragment.tag, "onLayout $left $top $right $bottom $changed")
                Log.d(this@BuyDialogFragment.tag, "onLayout measured $measuredWidth $measuredHeight")
                if (changed) {
                    onWidth(right - left)
                }
            }

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                Log.d(this@BuyDialogFragment.tag, "onMeasure ${MeasureSpec.toString(widthMeasureSpec)} ${MeasureSpec.toString(heightMeasureSpec)}")
//                val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//                when (widthMode) {
//                    MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> {
//                        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
//                    }
//                    else -> {
//                        // Do nothing
//                    }
//                }
                setMeasuredDimension(frameWidth, frameHeight)
            }
        }
        mainFrame.addView(frameLayout, frameWidth, frameHeight)
        return mainFrame
    }

    data class State(val width: Int, val isResumed: Boolean) {
        fun withWidth(value: Int) = State(value, isResumed)
        fun withResumed(value: Boolean) = State(width, value)
    }

    var state = State(-1, false)
        set(value) {
            field = value
            Log.d(tag, "setState $value")
            presentation?.cancel()
            if (state.width > 0 && state.isResumed) {
                val human = AndroidHuman(activity)
                val pole = Pole(state.width.toFloat(), 0f, 0, FrameLayoutScreen(frameLayout!!, human, activity))
                presentation = ui!!.present(human, pole, object : Div.Observer {
                    override fun onHeight(height: Float) {
                        val freshHeight = height.toInt()
                        Log.d(BuyDialogFragment.TAG, "onHeight: $freshHeight")
                        if (frameHeight != freshHeight) {
                            frameHeight = freshHeight
                            val layoutParams = frameLayout!!.layoutParams
                            layoutParams.height = freshHeight
                            frameLayout!!.layoutParams = layoutParams
                            frameLayout!!.requestLayout()
                        }
                    }

                    override fun onReaction(reaction: Reaction) {
                        Log.d(tag, "onReaction: $reaction")
                    }

                    override fun onError(throwable: Throwable) {
                        Log.e(TAG, "onError: $throwable", throwable)
                    }
                })
            }
        }

    fun onWidth(width: Int) {
        Log.d(tag, "onWidth $width")
        state = state.withWidth(width)
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
        state = state.withResumed(true)
    }

    override fun onPause() {
        state = state.withResumed(false)
        super.onPause()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}
