package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import com.rubyhuntersky.peregrine.utility.DefaultParcelable
import com.rubyhuntersky.peregrine.utility.read
import com.rubyhuntersky.peregrine.utility.write
import java.math.BigDecimal

/**
 * @author wehjin
 * *
 * @since 2/4/16.
 */

data class BuyProgram(val budget: BigDecimal,
                      val products: List<AssetPrice>, val productIndex: Int,
                      val accounts: List<FundingAccount>, val accountIndex: Int = 0,
                      val assetIndex: Int = 0) : DefaultParcelable, FundingProgram {

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

    fun withProductIndex(index: Int): BuyProgram = BuyProgram(budget, products, index, accounts, accountIndex, assetIndex)
    fun withAccountIndex(index: Int): BuyProgram = BuyProgram(budget, products, productIndex, accounts, index, assetIndex)
    fun withAssetIndex(index: Int): BuyProgram = BuyProgram(budget, products, productIndex, accounts, accountIndex, index)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.write(budget, products, productIndex, accounts, accountIndex, assetIndex)
    }

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = DefaultParcelable.generateCreator {
            BuyProgram(it.read(), it.read(), it.read(), it.read(), it.read(), it.read())
        }
    }

}
