package com.rubyhuntersky.peregrine.model

import android.os.Parcel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
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
                    override fun getAccountName(): String {
                        throw UnsupportedOperationException()
                    }

                    override fun getCashAvailable(): BigDecimal {
                        return BigDecimal.TEN
                    }

                    override fun getFundingOptions(exclude: String?): MutableList<FundingOption> {
                        throw UnsupportedOperationException()
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
        val buyIntention = BuyIntention(BigDecimal.TEN, mock(AssetPrice::class.java))
        assertTrue(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }

    @Test
    fun testHasFundsForBuyIntention_isTrue_whenCashExceedsBuyAmount() {
        val buyIntention = BuyIntention(BigDecimal.ONE, mock(AssetPrice::class.java))
        assertTrue(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }

    @Test
    fun testHasFundsForBuyIntention_isFalse_whenBuyAmountExceedsCash() {
        val buyIntention = BuyIntention(BigDecimal.TEN.add(BigDecimal.TEN), mock(AssetPrice::class.java))
        assertFalse(baseFundingAccount.hasFundsForBuyIntention(buyIntention))
    }
}