package com.rubyhuntersky.peregrine;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 12/1/15.
 */

public class AccountBalance {

    public final BigDecimal netCash;

    public AccountBalance(BigDecimal netCash) {
        this.netCash = netCash;
    }

    public AccountBalance(JSONObject balance) throws JSONException {
        final JSONObject accountBalance = balance.getJSONObject("accountBalance");
        final double netCash = accountBalance.getDouble("netCash");
        this.netCash = new BigDecimal(netCash);
    }
}
