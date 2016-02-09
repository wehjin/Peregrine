package com.rubyhuntersky.peregrine;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wehjin
 * @since 2/5/16.
 */

public interface FundingProgram {
    List<? extends FundingAccount> getFundingAccounts();
    int getSelectedFundingAccount();
    void setSelectedFundingAccount(int selection);

    boolean fundingAccountHasSufficientFundsToBuy();
    BigDecimal getAdditionalFundsNeededToBuy();
    List<? extends FundingOption> getFundingOptions();
    int getSelectedFundingOption();
    void setSelectedFundingOption(int selection);

    BigDecimal getSharesToSellForFunding();
    BigDecimal getAdditionalFundsNeededAfterSale();
}
