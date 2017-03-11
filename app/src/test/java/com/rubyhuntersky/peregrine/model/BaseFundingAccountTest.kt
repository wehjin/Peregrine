package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

/**
 * @author Jeffrey Yu
 * *
 * @since 6/4/16.
 */

class BaseFundingAccountTest {

    lateinit var baseFundingAccount: BaseFundingAccount

    @Before
    fun setUp() {
        baseFundingAccount =
                object : BaseFundingAccount() {
                    override val accountName: String
                        get() = throw UnsupportedOperationException()

                    override val cashAvailable: BigDecimal
                        get() = BigDecimal.TEN

                    override fun getFundingOptions(exclude: String): List<FundingOption> {
                        throw UnsupportedOperationException("not implemented")
                    }

                    override fun writeToParcel(dest: Parcel?, flags: Int) {
                        throw UnsupportedOperationException()
                    }

                    override fun describeContents(): Int {
                        throw UnsupportedOperationException()
                    }
                }
    }

    @Test
    fun testHasFundsForBuyIntention_isTrue_whenCashEqualsBuyAmount() {
        val buyIntention = BuyIntention(BigDecimal.TEN, AssetNamePrice("Dollar"))
        assertTrue(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }

    @Test
    fun testHasFundsForBuyIntention_isTrue_whenCashExceedsBuyAmount() {
        val buyIntention = BuyIntention(BigDecimal.ONE, AssetNamePrice("Dollar"))
        assertTrue(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }

    @Test
    fun testHasFundsForBuyIntention_isFalse_whenBuyAmountExceedsCash() {
        val buyIntention = BuyIntention(BigDecimal.TEN.add(BigDecimal.TEN), AssetNamePrice("Dollar"))
        assertFalse(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }
}