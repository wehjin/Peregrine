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
    val assets: List<FundingOption> get() {
        return if (account == null) {
            emptyList<FundingOption>()
        } else {
            val product = if (products.isEmpty()) null else products[productIndex]
            val exclude = product?.name ?: "nothing-to-exclude"
            account!!.getFundingOptions(exclude)
        }
    }
    val asset: FundingOption? get() = if (assets.isEmpty()) null else assets[assetIndex]

    fun withProductIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, index, accounts, accountIndex, assetIndex)
    }

    fun withAccountIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, productIndex, accounts, index, assetIndex)
    }

    fun withAssetIndex(index: Int): BuyProgram {
        return BuyProgram(budget, products, productIndex, accounts, accountIndex, index)
    }

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
