package com.rubyhuntersky.peregrine;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 2/6/16.
 */
public interface FundingOption {
    String getAssetName();
    BigDecimal getSharesAvailableToSell();
    BigDecimal getSellPrice();
    BigDecimal getValueWhenSold();
}
