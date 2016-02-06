package com.rubyhuntersky.peregrine;

import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wehjin
 * @since 2/6/16.
 */
public interface FundingAccount extends Parcelable {
    String getAccountName();
    BigDecimal getCashAvailable();
    List<FundingOption> getFundingOptions(String exclude);
}
