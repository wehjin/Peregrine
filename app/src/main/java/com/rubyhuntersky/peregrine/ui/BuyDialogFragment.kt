package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rubyhuntersky.coloret.Coloret.BLACK
import com.rubyhuntersky.gx.Gx.colorColumn
import com.rubyhuntersky.gx.Gx.dropDownMenuDiv
import com.rubyhuntersky.gx.Gx.gapColumn
import com.rubyhuntersky.gx.Gx.textColumn
import com.rubyhuntersky.gx.Gx.textTile
import com.rubyhuntersky.gx.android.AndroidGx.spinnerTile
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
import com.rubyhuntersky.gx.uis.divs.Div2
import com.rubyhuntersky.gx.uis.tiles.Tile0
import com.rubyhuntersky.gx.uis.tiles.Tile1
import com.rubyhuntersky.gx.uis.tiles.TileCreator
import com.rubyhuntersky.gx.uis.tiles.TileCreator.textTile1
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.*
import com.rubyhuntersky.peregrine.ui.UiHelper.getCurrencyDisplayString
import java.math.BigDecimal
import java.util.*

/**
 * @author wehjin
 * *
 * @since 1/19/16.
 */
class BuyDialogFragment() : TradeDialogFragment() {

    companion object {

        val DIVISION_SIGN = "\u00f7"
        val SPACING = gapColumn(Sizelet.QUARTER_FINGER)
        val DIVIDER = colorColumn(readables(.25f), BLACK)
        val TAG = BuyDialogFragment::class.java.simpleName
        val PROGRAM_KEY = "programKey"
        val DIVISION_SIGN_TILE = textTile(DIVISION_SIGN, IMPORTANT_DARK)
        val BUY_PRICES_SPINNER = "buyPricesSpinner"
        val FUNDING_ACCOUNT_SPINNER = "fundingAccountSpinner"
        val FUNDING_OPTION_SPINNER = "fundingOptionSpinner"

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

    private var program: BuyProgram? = null
    private fun BigDecimal.toCurrencyString(): String = getCurrencyDisplayString(this)
    private fun BigDecimal.toSharesString(): String = getSharesString(this)

    private fun FundingStatus.toOptionString(): String {
        return if (isFullyFunded) {
            "Sufficient funds ${cashAvailable.toCurrencyString()}"
        } else {
            "Buy ${fundedShares.toSharesString()} or Add funds ${shortfall.toCurrencyString()}"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PROGRAM_KEY, program)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)

        program = arguments.getParcelable<BuyProgram>(PROGRAM_KEY)
        if (program == null) {
            return
        }

        val buyAmount = program!!.budget
        val buyAssetPrices: List<String> = getBuyPrices(program!!.products)

        ui = Div0.create(object : Div.OnPresent {
            override fun onPresent(presenter: Div.Presenter) {
                presenter.addPresentation(object : Div.PresenterPresentation(presenter) {

                    var presentation: Div.Presentation? = null
                    var purchase = program!!

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

                    fun FundingAccount.getSharesPurchaseableWithCash(product: AssetPrice): String {
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
                                .expandDown(if (hasFundsForPurchase(budget)) {
                                    textColumn("Sufficient funds ${cashAvailable.currency}", READABLE_DARK)
                                } else {
                                    textColumn("Buy ${getSharesPurchaseableWithCash(product)} shares", READABLE_DARK)
                                            .expandDown(textColumn("or", READABLE_DARK))
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
                            val sharesInBudget = divider.expandDown(textColumn("${purchase.sharesInBudget.intString} shares", IMPORTANT_DARK))
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
                                        .expandDown(textColumn("Sell ${asset.getSharesToRaiseFunds(shortfall)} shares", IMPORTANT_DARK))
                            }

                            return productDecision
                                    .expandDown(accountDecision)
                                    .expandDown(assetDecision)
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


//        val ui = getBuyUi(buyAmount, buyAssetPrices)
//                .expandDown(gapColumn(FINGER))
//                .expandDown(fundingUi)
//                .expandVertical(TWO_THIRDS_FINGER)
//                .padHorizontal(THIRD_FINGER)
//                .placeBefore(colorColumn(PREVIOUS, WHITE), 0)
//                .printReadEval(object : Div4.Repl<Int, String, Int, Div0> {
//
//                    private var fundingAccountSelection = program!!.selectedFundingAccount
//                    private var buyPriceSelection = program!!.productIndex
//                    private var fundingPriceSelection = program!!.selectedFundingOption
//
//                    override fun print(unbound: Div4<Int, String, Int, Div0>): Div0 {
//                        return unbound.bind(program!!.productIndex)
//                                .bind(getSharesString(program!!.sharesToBuy))
//                                .bind(program!!.selectedFundingAccount)
//                                .bind(if (program!!.fundingAccountHasSufficientFundsToBuy() || program!!.fundingOptions.size == 0) {
//                                    Div0.EMPTY
//                                } else {
//                                    getSellUi(getFundingPrices(program!!.fundingOptions),
//                                            program!!.selectedFundingOption,
//                                            program!!.sharesToSellForFunding,
//                                            program!!.additionalFundsNeededAfterSale)
//                                })
//                    }
//
//                    override fun read(reaction: Reaction) {
//                        Log.d(TAG, "Repl reaction: " + reaction)
//                        if (BUY_PRICES_SPINNER == reaction.source) {
//
//                            buyPriceSelection = (reaction as ItemSelectionReaction<*>).item as Int
//                        }
//                        if (FUNDING_ACCOUNT_SPINNER == reaction.source) {
//
//                            fundingAccountSelection = (reaction as ItemSelectionReaction<*>).item as Int
//                        }
//                        if (FUNDING_OPTION_SPINNER == reaction.source) {
//                            fundingPriceSelection = (reaction as ItemSelectionReaction<*>).item as Int
//                        }
//                    }
//
//                    override fun eval(): Boolean {
//                        if (buyPriceSelection == program!!.productIndex
//                                && fundingAccountSelection == program!!.selectedFundingAccount
//                                && fundingPriceSelection == program!!.selectedFundingOption)
//                            return false
//
//                        //program!!.productIndex = buyPriceSelection
//                        program!!.selectedFundingAccount = fundingAccountSelection
//                        program!!.selectedFundingOption = fundingPriceSelection
//                        return true
//                    }
//                })

    }

    private fun getBuyUi(buyAmount: BigDecimal, buyPrices: List<String>): Div2<Int, String> {
        return textColumn("Buy " + getCurrencyDisplayString(buyAmount), IMPORTANT_DARK)
                .expandDown(SPACING)
                .expandDown(spinnerTile(buyPrices).name(BUY_PRICES_SPINNER).expandLeft(DIVISION_SIGN_TILE).toColumn())
                .expandDown(SPACING).expandDown(DIVIDER).expandDown(SPACING).expandDown(textTile1(IMPORTANT_DARK).toColumn())
    }

    private fun getBuyPrices(buyOptions: List<AssetPrice>): List<String> {
        val buyPrices = ArrayList<String>()
        for (price in buyOptions) {
            buyPrices.add(price.name + " " + getCurrencyDisplayString(price.price))
        }
        return buyPrices
    }

    private val fundingUi: Div2<Int, Div0>
        get() {
            val accountNamesAndStatus = ArrayList<Pair<String, String>>()
            val buyIntention = program!!.buyIntention!!
            for (fundingAccount in program!!.fundingAccounts) {
                val name = "Account " + fundingAccount.accountName
                val buyStatus = fundingAccount.getStatusForBuy(buyIntention)
                accountNamesAndStatus.add(Pair.create(name, buyStatus.toOptionString()))
            }

            val accountTile = Tile1.create(object : Tile1.OnBind<Pair<String, String>> {

                private val tile = textTile1(IMPORTANT_DARK).expandDown(TileCreator.READABLE_GAP).expandDown(textTile1(READABLE_DARK))

                override fun onBind(condition: Pair<String, String>): Tile0 {
                    return tile.bind(condition.first).bind(condition.second)
                }
            })


            val accountSpinnerTile = spinnerTile(accountTile, accountNamesAndStatus)
            return accountSpinnerTile.name(FUNDING_ACCOUNT_SPINNER).toColumn().expandDown()
        }

    private fun getFundingPrices(fundingOptions: List<FundingOption>): List<String> {
        val fundingOptionPrices = ArrayList<String>()
        for (fundingOption in fundingOptions) {
            fundingOptionPrices.add(fundingOption.assetName + " " + getCurrencyDisplayString(fundingOption.sellPrice))
        }
        return fundingOptionPrices
    }

    private fun getSellUi(sellOptions: List<String>, selectedSellOption: Int, sharesToSell: BigDecimal, shortfall: BigDecimal): Div0 {
        val shortfallExpansion = if (shortfall.compareTo(BigDecimal.ZERO) <= 0)
            Div0.EMPTY
        else
            SPACING.expandDown(textColumn("Shortfall " + getCurrencyDisplayString(shortfall), READABLE_DARK))

        return spinnerTile(sellOptions, selectedSellOption).name(FUNDING_OPTION_SPINNER).expandLeft(DIVISION_SIGN_TILE).toColumn().expandDown(SPACING).expandDown(DIVIDER).expandDown(SPACING).expandDown(textColumn("Sell " + sharesToSell.setScale(0, BigDecimal.ROUND_CEILING) + " shares", IMPORTANT_DARK)).expandDown(shortfallExpansion)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(tag, "onCreateView")
        val windowSize = Point()
        activity.windowManager.defaultDisplay.getSize(windowSize)
        val width = windowSize.x - 40
        val height = windowSize.y - 20

        val mainFrame = inflater!!.inflate(R.layout.fragment_buy, container, false) as FrameLayout
        frameLayout = object : FrameLayout(activity) {
            override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                super.onLayout(changed, left, top, right, bottom)
                Log.d(this@BuyDialogFragment.tag, "onLayout $left $top $right $bottom $changed")
                if (changed) {
                    onWidth(right - left)
                }
            }

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                setMeasuredDimension(width, height)
            }
        }
        mainFrame.addView(frameLayout, width, height)
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
                presentUi()
            }
        }

    private fun presentUi() {
        val human = AndroidHuman(activity)
        val pole = Pole(state.width.toFloat(), 0f, 0, FrameLayoutScreen(frameLayout!!, human))

        presentation = ui!!.present(human, pole, object : Div.Observer {
            override fun onHeight(height: Float) {
                Log.d(tag, "onHeight: $height")
                frameLayout!!.requestLayout()
            }

            override fun onReaction(reaction: Reaction) {
                Log.d(tag, "onReaction: $reaction")
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, "onError: $throwable", throwable)
            }
        })
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
