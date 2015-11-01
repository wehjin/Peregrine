package com.rubyhuntersky.peregrine;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class EtradeAccount {

    private String description;
    private String accountId;
    private BigDecimal netAccountValue;

    public EtradeAccount(String description, String accountId, BigDecimal netAccountValue) {
        this.description = description;
        this.accountId = accountId;
        this.netAccountValue = netAccountValue;
    }

    public BigDecimal getNetAccountValue() {
        return netAccountValue;
    }
}
