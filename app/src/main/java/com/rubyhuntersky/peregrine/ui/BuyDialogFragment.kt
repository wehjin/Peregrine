package com.rubyhuntersky.peregrine.ui

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.coloret.Coloret.BLACK
import com.rubyhuntersky.coloret.Coloret.WHITE
import com.rubyhuntersky.gx.Gx.*
import com.rubyhuntersky.gx.android.AndroidGx.spinnerTile
import com.rubyhuntersky.gx.android.PoleView
import com.rubyhuntersky.gx.basics.Sizelet
import com.rubyhuntersky.gx.basics.Sizelet.*
import com.rubyhuntersky.gx.basics.TextStylet.IMPORTANT_DARK
import com.rubyhuntersky.gx.basics.TextStylet.READABLE_DARK
import com.rubyhuntersky.gx.observers.Observer
import com.rubyhuntersky.gx.presentations.EmptyPresentation
import com.rubyhuntersky.gx.presentations.Presentation
import com.rubyhuntersky.gx.reactions.HeightChangedReaction
import com.rubyhuntersky.gx.reactions.ItemSelectionReaction
import com.rubyhuntersky.gx.reactions.Reaction
import com.rubyhuntersky.gx.uis.divs.Div0
import com.rubyhuntersky.gx.uis.divs.Div2
import com.rubyhuntersky.gx.uis.divs.Div4
import com.rubyhuntersky.gx.uis.tiles.Tile0
import com.rubyhuntersky.gx.uis.tiles.Tile1
import com.rubyhuntersky.gx.uis.tiles.TileCreator
import com.rubyhuntersky.gx.uis.tiles.TileCreator.textTile1
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AssetPrice
import com.rubyhuntersky.peregrine.model.BuyProgram
import com.rubyhuntersky.peregrine.model.FundingAccount
import com.rubyhuntersky.peregrine.model.FundingOption
import com.rubyhuntersky.peregrine.ui.UiHelper.getCurrencyDisplayString
import java.math.BigDecimal
import java.util.*

/**
 * @author wehjin
 * *
 * @since 1/19/16.
 */
class BuyDialogFragment : TradeDialogFragment() {

    private var columnUiView: PoleView? = null
    private var presentation: Presentation = EmptyPresentation()
    private var ui: Div0? = null
    private var program: BuyProgram? = null

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

        ui = getBuyUi(program!!.buyAmount, getBuyPrices(program!!.buyOptions)).expandDown(gapColumn(Sizelet.FINGER)).expandDown(fundingUi).expandVertical(TWO_THIRDS_FINGER).padHorizontal(THIRD_FINGER).placeBefore(colorColumn(PREVIOUS, WHITE), 0).printReadEval(object : Div4.Repl<Int, String, Int, Div0> {

            private var fundingAccountSelection = program!!.selectedFundingAccount
            private var buyPriceSelection = program!!.selectedBuyOption
            private var fundingPriceSelection = program!!.selectedFundingOption

            override fun print(div4: Div4<Int, String, Int, Div0>): Div0 {
                return div4.bind(program!!.selectedBuyOption).bind(TradeDialogFragment.Companion.getSharesString(program!!.sharesToBuy!!)).bind(program!!.selectedFundingAccount).bind(if (program!!.fundingAccountHasSufficientFundsToBuy() || program!!.fundingOptions.size == 0)
                    Div0.EMPTY
                else
                    getSellUi(getFundingPrices(program!!.fundingOptions),
                            program!!.selectedFundingOption,
                            program!!.sharesToSellForFunding,
                            program!!.additionalFundsNeededAfterSale))
            }

            override fun read(reaction: Reaction) {
                Log.d(TAG, "Repl reaction: " + reaction)
                if (BUY_PRICES_SPINNER == reaction.source) {

                    buyPriceSelection = (reaction as ItemSelectionReaction<*>).item as Int
                }
                if (FUNDING_ACCOUNT_SPINNER == reaction.source) {

                    fundingAccountSelection = (reaction as ItemSelectionReaction<*>).item as Int
                }
                if (FUNDING_OPTION_SPINNER == reaction.source) {
                    fundingPriceSelection = (reaction as ItemSelectionReaction<*>).item as Int
                }
            }

            override fun eval(): Boolean {
                if (buyPriceSelection == program!!.selectedBuyOption
                        && fundingAccountSelection == program!!.selectedFundingAccount
                        && fundingPriceSelection == program!!.selectedFundingOption)
                    return false

                program!!.selectedBuyOption = buyPriceSelection
                program!!.selectedFundingAccount = fundingAccountSelection
                program!!.selectedFundingOption = fundingPriceSelection
                return true
            }
        })

    }

    private fun BigDecimal.asCurrencyString(): String = getCurrencyDisplayString(this)
    private val FundingAccount.additionalFundsNeededToBuyProgram: BigDecimal get() = program!!.getAdditionalFundsNeededToBuy(this)!!
    private val FundingAccount.hasSufficientFundsToBuyProgram: Boolean get() = program!!.fundingAccountHasSufficientFundsToBuy(this)

    private fun getFundingAccountStatus(fundingAccount: FundingAccount): String {
        return if (fundingAccount.hasSufficientFundsToBuyProgram) {
            "Sufficient funds ${fundingAccount.cashAvailable.asCurrencyString()}"
        } else {
            "Add funds ${fundingAccount.additionalFundsNeededToBuyProgram.asCurrencyString()}"
        }
    }

    private fun getBuyUi(buyAmount: BigDecimal, buyPrices: List<String>): Div2<Int, String> {
        return textColumn("Buy " + getCurrencyDisplayString(buyAmount), IMPORTANT_DARK).expandDown(SPACING).expandDown(spinnerTile(buyPrices).name(BUY_PRICES_SPINNER).expandLeft(DIVISION_SIGN_TILE).toColumn()).expandDown(SPACING).expandDown(DIVIDER).expandDown(SPACING).expandDown(textTile1(IMPORTANT_DARK).toColumn())
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
            for (fundingAccount in program!!.fundingAccounts) {
                val name = "Account " + fundingAccount.accountName
                val status = getFundingAccountStatus(fundingAccount)
                accountNamesAndStatus.add(Pair.create(name, status))
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
        val inflate = inflater!!.inflate(R.layout.fragment_buy, container, false)
        columnUiView = inflate.findViewById(R.id.ui) as PoleView
        return inflate
    }

    override fun onResume() {
        super.onResume()
        present()
    }

    private fun present() {
        presentation.cancel()
        presentation = columnUiView!!.present(ui, object : Observer {
            override fun onReaction(reaction: Reaction) {
                Log.d(TAG, "onReaction: " + reaction)
                if (reaction is HeightChangedReaction) {
                    columnUiView!!.clearVariableDimensions()
                    columnUiView!!.requestLayout()
                }
            }

            override fun onEnd() {
                Log.d(TAG, "onEnd")
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, "onError", throwable)
            }
        })
    }

    override fun onPause() {
        presentation.cancel()
        super.onPause()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    companion object {

        val DIVISION_SIGN = "\u00f7"
        val SPACING = gapColumn(Sizelet.QUARTER_FINGER)
        val DIVIDER = colorColumn(readables(.1f), BLACK)
        val TAG = BuyDialogFragment::class.java.simpleName
        val PROGRAM_KEY = "programKey"
        val DIVISION_SIGN_TILE = textTile(DIVISION_SIGN, IMPORTANT_DARK)
        val BUY_PRICES_SPINNER = "buyPricesSpinner"
        val FUNDING_ACCOUNT_SPINNER = "fundingAccountSpinner"
        val FUNDING_OPTION_SPINNER = "fundingOptionSpinner"

        fun create(amount: BigDecimal, prices: List<AssetPrice>, selectedPrice: Int, fundingAccounts: List<FundingAccount>?): BuyDialogFragment {
            val buyProgram = BuyProgram(amount, prices, selectedPrice)
            if (fundingAccounts != null) {
                buyProgram.setFundingAccounts(fundingAccounts, 0, 0)
            }

            val arguments = Bundle()
            arguments.putParcelable(PROGRAM_KEY, buyProgram)

            val fragment = BuyDialogFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
