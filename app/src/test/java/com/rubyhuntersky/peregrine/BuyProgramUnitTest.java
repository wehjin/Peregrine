package com.rubyhuntersky.peregrine;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BuyProgramUnitTest {

    private BigDecimal buyAmount;
    private List<AssetPrice> buyOptions;
    private BuyProgram buyProgram;

    @Before
    public void setUp() throws Exception {
        buyAmount = BigDecimal.valueOf(100);
        buyOptions = Collections.singletonList(new AssetPrice("asset1", BigDecimal.valueOf(25)));
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
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance);

        buyProgram.setFundingAccounts(Collections.singletonList(fundingAccount), 0, -1);
        final BigDecimal fundsNeeded = buyProgram.getAdditionalFundsNeededToBuy();
        assertEquals(buyAmount.doubleValue(), fundsNeeded.doubleValue(), .0001);
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoPositionsAndInsufficientFunds() throws Exception {
        final BigDecimal cashAmount = BigDecimal.valueOf(40);
        final AccountBalance accountBalance = new AccountBalance(cashAmount);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", "test1", "unittest", cashAmount);
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance);

        buyProgram.setFundingAccounts(Collections.singletonList(fundingAccount), 0, -1);
        final BigDecimal fundsNeeded = buyProgram.getAdditionalFundsNeededToBuy();
        assertEquals(buyAmount.subtract(cashAmount).doubleValue(), fundsNeeded.doubleValue(), .0001);
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoPositionsAndSufficientFunds() throws Exception {
        final BigDecimal cashAmount = BigDecimal.valueOf(200);
        final AccountBalance accountBalance = new AccountBalance(cashAmount);
        final EtradeAccount etradeAccount = new EtradeAccount("Test", "test1", "unittest", cashAmount);
        final FundingAccount fundingAccount = etradeAccount.toFundingAccount(accountBalance);

        buyProgram.setFundingAccounts(Collections.singletonList(fundingAccount), 0, -1);
        final BigDecimal fundsNeeded = buyProgram.getAdditionalFundsNeededToBuy();
        assertEquals(0, fundsNeeded.doubleValue(), .0001);
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndInsufficientSaleableAssets() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndSufficientSaleableAssets() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void program_computesFundsNeeded_whenAccountHasNoFundsAndSamePosition() throws Exception {
        fail("Not yet implemented");
    }
}