package com.rubyhuntersky.peregrine;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wehjin
 * @since 11/28/15.
 */

public class Asset {
    public String symbol;
    public String typeCode;
    public String description;
    public String direction;
    public BigDecimal quantity;
    public BigDecimal currentPrice;
    public BigDecimal marketValue;
    public BigDecimal purchaseValue;
    public String accountId;
    public String accountDescription;
    public Date arrivalTime;

    public Asset(JSONObject jsonObject, String accountId, String accountDescription,
          Date arrivalTime) throws JSONException {
        final JSONObject productId = jsonObject.getJSONObject("productId");
        symbol = productId.getString("symbol");
        typeCode = productId.getString("typeCode");
        description = jsonObject.getString("description");
        direction = jsonObject.getString("longOrShort");
        quantity = new BigDecimal(jsonObject.getLong("qty"));
        currentPrice = new BigDecimal(jsonObject.getString("currentPrice"));
        marketValue = new BigDecimal(jsonObject.getString("marketValue"));
        purchaseValue = new BigDecimal(jsonObject.getString("costBasis"));
        this.accountId = accountId;
        this.accountDescription = accountDescription;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "Asset{" +
              "symbol='" + symbol + '\'' +
              ", typeCode='" + typeCode + '\'' +
              ", description='" + description + '\'' +
              ", direction='" + direction + '\'' +
              ", quantity=" + quantity +
              ", currentPrice=" + currentPrice +
              ", marketValue=" + marketValue +
              ", purchaseValue=" + purchaseValue +
              ", accountId='" + accountId + '\'' +
              ", accountDescription='" + accountDescription + '\'' +
              ", arrivalTime=" + arrivalTime +
              '}';
    }
}
