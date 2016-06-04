package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.util.*

/**
 * @author wehjin
 * *
 * @since 2/4/16.
 */

data class BuyProgram(val buyAmount: BigDecimal, val buyOptions: List<AssetPrice>, var selectedBuyOption: Int) : Parcelable, FundingProgram {

    private var fundingAccounts = emptyList<FundingAccount>()
    private var selectedFundingAccount: Int = 0
    private var selectedFundingOption: Int = 0

    fun AssetPrice.toIntention(buyAmount: BigDecimal): BuyIntention = BuyIntention(buyAmount, this)

    val buyPrice: BigDecimal get() = buyOption!!.price
    val buyOption: AssetPrice? get() = if (selectedBuyOption < 0) null else buyOptions[selectedBuyOption]
    val buyIntention: BuyIntention? get() = buyOption?.toIntention(buyAmount)
    val sharesToBuy: BigDecimal get() = getSharesAvailableToBuyWithFunds(buyAmount)

    fun getSharesAvailableToBuyWithFunds(funds: BigDecimal): BigDecimal = funds.divide(buyPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP)

    fun setFundingAccounts(fundingAccounts: List<FundingAccount>, selectedFundingAccount: Int, selectedFundingOption: Int) {
        this.fundingAccounts = fundingAccounts
        this.selectedFundingAccount = selectedFundingAccount
    }

    override fun getFundingAccounts(): List<FundingAccount> {
        return fundingAccounts
    }

    override fun getSelectedFundingAccount(): Int {
        return selectedFundingAccount
    }

    override fun setSelectedFundingAccount(selection: Int) {
        selectedFundingAccount = selection
    }

    override fun fundingAccountHasSufficientFundsToBuy(): Boolean {
        val currentFundingAccount = fundingAccount ?: return false
        return fundingAccountHasSufficientFundsToBuy(currentFundingAccount)
    }

    override fun fundingAccountHasSufficientFundsToBuy(fundingAccount: FundingAccount): Boolean {
        val currentBuyIntention = buyIntention ?: return false
        return fundingAccount.hasFundsForBuyIntention(currentBuyIntention)
    }

    override fun getAdditionalFundsNeededToBuy(): BigDecimal {
        return getAdditionalFundsNeededToBuy(fundingAccount)!!
    }

    override fun getAdditionalFundsNeededToBuy(fundingAccount: FundingAccount?): BigDecimal? {
        if (fundingAccount == null)
            return null
        val cashAvailableInFundingAccount = fundingAccount.cashAvailable
        return buyAmount.subtract(cashAvailableInFundingAccount).max(BigDecimal.ZERO)
    }

    override fun getFundingOptions(): List<FundingOption> {
        val buyOption = buyOption
        val fundingAccount = fundingAccount
        if (fundingAccount == null || buyOption == null)
            return emptyList()
        return fundingAccount.getFundingOptions(buyOption.name)
    }

    override fun getSelectedFundingOption(): Int {
        return selectedFundingOption
    }

    override fun setSelectedFundingOption(selection: Int) {
        selectedFundingOption = selection
    }

    override fun getSharesToSellForFunding(): BigDecimal {
        val fundingOption = fundingOption ?: return BigDecimal.ZERO

        val fundsNeeded = additionalFundsNeededToBuy
        val sharesToSell = fundsNeeded.divide(fundingOption.sellPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP)
        return sharesToSell.min(fundingOption.sharesAvailableToSell).setScale(0, BigDecimal.ROUND_CEILING)
    }

    override fun getAdditionalFundsNeededAfterSale(): BigDecimal {
        val fundingOption = fundingOption ?: return buyAmount

        val fundedAmount = sharesToSellForFunding.multiply(fundingOption.sellPrice)
        val additionalFundsNeededToBuy = additionalFundsNeededToBuy
        return additionalFundsNeededToBuy.subtract(fundedAmount).max(BigDecimal.ZERO)
    }

    val fundingOption: FundingOption?
        get() {
            val fundingOptions = fundingOptions
            return if (selectedFundingOption < 0 || selectedFundingOption >= fundingOptions.size)
                null
            else
                fundingOptions[selectedFundingOption]
        }

    val fundingAccount: FundingAccount?
        get() = if (selectedFundingAccount < 0) null else fundingAccounts[selectedFundingAccount]

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(buyAmount)
        dest.writeList(buyOptions)
        dest.writeInt(selectedBuyOption)
        dest.writeList(fundingAccounts)
        dest.writeInt(selectedFundingAccount)
        dest.writeInt(selectedFundingOption)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<BuyProgram> = object : Parcelable.Creator<BuyProgram> {

            fun <T> Parcel.readList(): List<T> {
                val list = ArrayList<T>()
                readList(list, null)
                return list
            }

            override fun createFromParcel(parcel: Parcel): BuyProgram {
                val buyAmount = parcel.readSerializable() as BigDecimal
                val buyOptions = parcel.readList<AssetPrice>()
                val selectedBuyOption = parcel.readInt()
                val buyProgram = BuyProgram(buyAmount, buyOptions, selectedBuyOption)

                val fundingAccounts = parcel.readList<FundingAccount>()
                val selectedFundingAccount = parcel.readInt()
                val selectedFundingOption = parcel.readInt()
                buyProgram.setFundingAccounts(fundingAccounts, selectedFundingAccount, selectedFundingOption)
                return buyProgram
            }

            override fun newArray(size: Int): Array<BuyProgram?> {
                return arrayOfNulls(size)
            }
        }
    }
}
