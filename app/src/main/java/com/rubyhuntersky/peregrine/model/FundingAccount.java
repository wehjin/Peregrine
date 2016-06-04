package com.rubyhuntersky.peregrine.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wehjin
 * @since 2/6/16.
 */
public interface FundingAccount extends Parcelable {
    @NonNull
    String getAccountName();
    @NonNull
    BigDecimal getCashAvailable();
    @NonNull
    List<FundingOption> getFundingOptions(String exclude);

    boolean hasFundsForBuyIntention(@NonNull BuyIntention buyIntention);

    @NonNull
    FundingStatus getStatusForBuy(@NonNull BuyIntention buyIntention);
}
