package com.rubyhuntersky.peregrine;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BuyProgramUnitTest {

    private BigDecimal buyAmount;
    private List<AssetPrice> buyOptions;
    private BuyProgram buyProgram;

    @Before
    public void setUp() throws Exception {
        buyAmount = BigDecimal.valueOf(100);
        buyOptions = singletonList(new AssetPrice("asset1", BigDecimal.valueOf(25)));
        buyProgram = new BuyProgram(buyAmount, buyOptions, 0);
    }

    @Test
    public void sharesCount_isTotalDividedByPrice() throws Exception {
        assertEquals(4, buyProgram.getSharesToBuy().intValue());
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoPositionsAndNoFunds() throws Exception {
        final BigDecimal cashAmount = BigDecimal.ZERO;
        final AccountBalance accountBalance = new AccountBalance(cashAmount);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", "test1", "unittest", cashAmount);
        final List<Asset> assets = emptyList();
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance, assets);

        buyProgram.setFundingAccounts(singletonList(fundingAccount), 0, 0);
        final BigDecimal fundsNeeded = buyProgram.getAdditionalFundsNeededToBuy();
        assertEquals(buyAmount.doubleValue(), fundsNeeded.doubleValue(), .0001);
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoPositionsAndInsufficientFunds() throws Exception {
        final BigDecimal cashAmount = BigDecimal.valueOf(40);
        final AccountBalance accountBalance = new AccountBalance(cashAmount);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", "test1", "unittest", cashAmount);
        final List<Asset> assets = emptyList();
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance, assets);

        buyProgram.setFundingAccounts(singletonList(fundingAccount), 0, 0);
        final BigDecimal fundsNeeded = buyProgram.getAdditionalFundsNeededToBuy();
        assertEquals(buyAmount.subtract(cashAmount).doubleValue(), fundsNeeded.doubleValue(), .0001);
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoPositionsAndSufficientFunds() throws Exception {
        final BigDecimal cashAmount = BigDecimal.valueOf(200);
        final AccountBalance accountBalance = new AccountBalance(cashAmount);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", "test1", "unittest", cashAmount);
        final List<Asset> assets = emptyList();
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance, assets);

        buyProgram.setFundingAccounts(singletonList(fundingAccount), 0, 0);
        assertEquals(BigDecimal.ZERO, buyProgram.getAdditionalFundsNeededToBuy());
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndInsufficientSaleableAssets() throws Exception {
        final String accountId = "test1";
        final AccountBalance accountBalance = new AccountBalance(BigDecimal.ZERO);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", accountId, "unittest", BigDecimal.ZERO);

        final BigDecimal assetValue = buyAmount.divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
        final Asset asset = new Asset(accountId, "saleable", assetValue, BigDecimal.ONE, assetValue);
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance, singletonList(asset));

        buyProgram.setFundingAccounts(singletonList(fundingAccount), 0, 0);
        assertEquals(buyAmount, buyProgram.getAdditionalFundsNeededToBuy());
        assertEquals(BigDecimal.ONE, buyProgram.getSharesToSellForFunding());
        assertEquals(buyAmount.subtract(assetValue), buyProgram.getAdditionalFundsNeededAfterSale());
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndSufficientSaleableAssets() throws Exception {
        final String accountId = "test1";
        final AccountBalance accountBalance = new AccountBalance(BigDecimal.ZERO);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", accountId, "unittest", BigDecimal.ZERO);

        final BigDecimal assetValue = buyAmount.multiply(BigDecimal.valueOf(2));
        final Asset asset = new Asset(accountId, "saleable", assetValue, BigDecimal.ONE, assetValue);
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance, singletonList(asset));

        buyProgram.setFundingAccounts(singletonList(fundingAccount), 0, 0);
        assertEquals("funds needed", buyAmount, buyProgram.getAdditionalFundsNeededToBuy());
        assertEquals("shares to sell", BigDecimal.ONE, buyProgram.getSharesToSellForFunding());
        assertEquals("funds needed after sale", BigDecimal.ZERO, buyProgram.getAdditionalFundsNeededAfterSale());
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndSamePosition() throws Exception {
        fail("Not yet implemented");
    }
}