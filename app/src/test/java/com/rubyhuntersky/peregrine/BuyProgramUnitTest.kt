package com.rubyhuntersky.peregrine

import com.rubyhuntersky.peregrine.model.AccountAssets
import com.rubyhuntersky.peregrine.model.Asset
import com.rubyhuntersky.peregrine.model.AssetPrice
import com.rubyhuntersky.peregrine.model.BuyProgram
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*
import java.util.Collections.emptyList

class BuyProgramUnitTest {

    private var buyAmount: BigDecimal? = null
    private var buyOptions: List<AssetPrice>? = null
    private var buyProgram: BuyProgram? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        buyAmount = BigDecimal.valueOf(100)
        buyOptions = listOf(AssetPrice("asset1", BigDecimal.valueOf(25)))
        buyProgram = BuyProgram(buyAmount!!, buyOptions!!, 0)
    }

    @Test
    @Throws(Exception::class)
    fun sharesCount_isTotalDividedByPrice() {
        assertEquals(4, buyProgram!!.sharesToBuy.toInt().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoPositionsAndNoFunds() {
        val cashAmount = BigDecimal.ZERO
        val assets = emptyList<Asset>()
        val fundingAccount = AccountAssets("test1", cashAmount, assets).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        val fundsNeeded = buyProgram!!.additionalFundsNeededToBuy
        assertEquals(buyAmount!!.toDouble(), fundsNeeded.toDouble(), .0001)
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoPositionsAndInsufficientFunds() {
        val cashAmount = BigDecimal.valueOf(40)
        val assets = emptyList<Asset>()
        val fundingAccount = AccountAssets("test1", cashAmount, assets).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        val fundsNeeded = buyProgram!!.additionalFundsNeededToBuy
        assertEquals(buyAmount!!.subtract(cashAmount).toDouble(), fundsNeeded.toDouble(), .0001)
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoPositionsAndSufficientFunds() {
        val cashAmount = BigDecimal.valueOf(200)
        val assets = emptyList<Asset>()
        val fundingAccount = AccountAssets("test1", cashAmount, assets).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        assertEquals(BigDecimal.ZERO, buyProgram!!.additionalFundsNeededToBuy)
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoFundsAndInsufficientSaleableAssets() {
        val accountId = "test1"
        val assetValue = buyAmount!!.divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP)
        val asset = Asset(accountId, "saleable", assetValue, BigDecimal.ONE, assetValue)
        val fundingAccount = AccountAssets(accountId, BigDecimal.ZERO, listOf(asset)).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        assertEquals("funds needed", buyAmount, buyProgram!!.additionalFundsNeededToBuy)
        assertEquals("shares to sell", BigDecimal.ONE, buyProgram!!.sharesToSellForFunding)
        assertEquals("funds needed after sale",
                buyAmount!!.subtract(assetValue),
                buyProgram!!.additionalFundsNeededAfterSale)
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoFundsAndSufficientSaleableAssets() {
        val accountId = "test1"
        val assetValue = buyAmount!!.multiply(BigDecimal.valueOf(2))
        val asset = Asset(accountId, "saleable", assetValue, BigDecimal.ONE, assetValue)
        val fundingAccount = AccountAssets(accountId, BigDecimal.ZERO, listOf(asset)).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        assertEquals("funds needed", buyAmount, buyProgram!!.additionalFundsNeededToBuy)
        assertEquals("shares to sell", BigDecimal.ONE, buyProgram!!.sharesToSellForFunding)
        assertEquals("funds needed after sale", BigDecimal.ZERO, buyProgram!!.additionalFundsNeededAfterSale)
    }

    @Test
    @Throws(Exception::class)
    fun program_computesFundsNeeded_whenAccountHasNoFundsAndSamePositionAsBuy() {
        val selectedBuyOption = buyOptions!![0]

        val accountId = "test1"
        val assetValue = buyAmount!!.multiply(BigDecimal.valueOf(2))
        val asset = Asset(accountId, selectedBuyOption.name, assetValue, BigDecimal.ONE, assetValue)
        val fundingAccount = AccountAssets(accountId, BigDecimal.ZERO, listOf(asset)).toFundingAccount()

        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        assertEquals("funds needed", buyAmount, buyProgram!!.additionalFundsNeededToBuy)
        assertEquals("shares to sell", BigDecimal.ZERO, buyProgram!!.sharesToSellForFunding)
        assertEquals("funds needed after sale", buyAmount, buyProgram!!.additionalFundsNeededAfterSale)
    }

    @Test
    @Throws(Exception::class)
    fun program_retrievesFundingOption_whenSelectedFundingOptionChanges() {
        val accountId = "testAccount1"
        val asset1 = Asset(accountId, "stock1", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        val asset2 = Asset(accountId, "stock2", BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE)
        val assets = ArrayList<Asset>()
        assets.add(asset1)
        assets.add(asset2)
        val fundingAccount = AccountAssets(accountId, BigDecimal.valueOf(11), assets).toFundingAccount()
        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        buyProgram!!.selectedFundingOption = 1
        val fundingOption = buyProgram!!.fundingOption
        assertEquals(BigDecimal.TEN, fundingOption!!.sharesAvailableToSell)
    }

    @Test
    @Throws(Exception::class)
    fun program_producesShortfall() {
        val accountId = "testAccount1"
        val assets = ArrayList<Asset>()
        assets.add(Asset(accountId, "stock1", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE))
        assets.add(Asset(accountId, "stock2", BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE))
        assets.add(Asset(accountId, "USD", BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE))
        val fundingAccount = AccountAssets(accountId, BigDecimal.TEN, assets).toFundingAccount()
        buyProgram!!.setFundingAccounts(listOf(fundingAccount), 0, 0)
        buyProgram!!.selectedFundingOption = 1
        val shortfall = buyProgram!!.additionalFundsNeededAfterSale
        assertEquals(BigDecimal.valueOf(80), shortfall)
    }
}