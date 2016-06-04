package com.rubyhuntersky.peregrine.model;

import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 2/6/16.
 */
public interface FundingOption extends Parcelable {
    String getAssetName();
    BigDecimal getSharesAvailableToSell();
    BigDecimal getSellPrice();
    BigDecimal getValueWhenSold();
}
