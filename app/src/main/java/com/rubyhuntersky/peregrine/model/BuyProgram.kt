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

data class BuyProgram(val budget: BigDecimal,
                      val products: List<AssetPrice>, val productIndex: Int,
                      val accounts: List<FundingAccount>, val accountIndex: Int = 0,
                      val assetIndex: Int = 0) : Parcelable, FundingProgram {

    val product: AssetPrice get() = products[productIndex]
    val sharesInBudget: BigDecimal get() = budget.divide(product.price, Values.SCALE, BigDecimal.ROUND_FLOOR)
    val account: FundingAccount? get() = if (accounts.isEmpty()) {
        null
    } else {
        accounts[accountIndex]
    }
    val assets: List<FundingOption> by lazy {
        if (account == null) {
            emptyList<FundingOption>()
        } else {
            val product = if (products.isEmpty()) null else products[productIndex]
            val exclude = product?.name ?: "nothing-to-exclude"
            account!!.getFundingOptions(exclude)
        }
    }
    val asset: FundingOption? = if (assets.isEmpty()) null else assets[assetIndex]

    fun withProductIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, index, accounts, accountIndex, assetIndex)
    }

    fun withAccountIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, productIndex, accounts, index, assetIndex)
    }

    fun withAssetIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, productIndex, accounts, accountIndex, index)
    }

    fun AssetPrice.toIntention(buyAmount: BigDecimal): BuyIntention = BuyIntention(buyAmount, this)

    val buyPrice: BigDecimal get() = buyOption!!.price
    val buyOption: AssetPrice? get() = if (productIndex < 0) null else products[productIndex]
    val buyIntention: BuyIntention? get() = buyOption?.toIntention(budget)
    val sharesToBuy: BigDecimal get() = getSharesAvailableToBuyWithFunds(budget)

    fun getSharesAvailableToBuyWithFunds(funds: BigDecimal): BigDecimal = funds.divide(buyPrice, Values.SCALE, BigDecimal.ROUND_HALF_UP)

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
        return budget.subtract(cashAvailableInFundingAccount).max(BigDecimal.ZERO)
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
        val fundingOption = fundingOption ?: return budget

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
        dest.writeSerializable(budget)
        dest.writeList(products)
        dest.writeInt(productIndex)
        dest.writeList(accounts)
        dest.writeInt(accountIndex)
        dest.writeInt(assetIndex)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<BuyProgram> = object : Parcelable.Creator<BuyProgram> {

            fun <T> Parcel.readList(): List<T> {
                val list = ArrayList<T>()
                readList(list, null)
                return list
            }

            override fun createFromParcel(parcel: Parcel): BuyProgram {
                val budget = parcel.readSerializable() as BigDecimal
                val products = parcel.readList<AssetPrice>()
                val productIndex = parcel.readInt()
                val accounts = parcel.readList<FundingAccount>()
                val accountIndex = parcel.readInt()
                val assetIndex = parcel.readInt()
                return BuyProgram(budget, products, productIndex, accounts, accountIndex, assetIndex)
            }

            override fun newArray(size: Int): Array<BuyProgram?> {
                return arrayOfNulls(size)
            }
        }
    }

}
