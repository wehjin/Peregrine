package com.rubyhuntersky.peregrine.model;

import android.os.Parcel;

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

    public boolean isInGroup(Group group, Assignments assignments) {
        final String partitionId = assignments.getPartitionId(symbol);
        return partitionId != null && partitionId.equals(group.getPartitionId());
    }

    public FundingOption toFundingOption() {
        return new AssetFundingOption(this);
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

    public Asset(String accountId, String symbol, BigDecimal marketValue, BigDecimal shares, BigDecimal currentPrice) {
        this.accountId = accountId;
        this.symbol = symbol;
        this.marketValue = marketValue;
        this.quantity = shares;
        this.currentPrice = currentPrice;
    }

    public Asset(JSONObject jsonObject, String accountId, String accountDescription, Date arrivalTime) throws JSONException {
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

    private static class AssetFundingOption implements FundingOption {

        private final String assetName;
        private final BigDecimal sharesAvailabletoSell;
        private final BigDecimal sellPrice;

        @Override
        public String getAssetName() {
            return assetName;
        }

        @Override
        public BigDecimal getSharesAvailableToSell() {
            return sharesAvailabletoSell;
        }

        @Override
        public BigDecimal getSellPrice() {
            return sellPrice;
        }

        @Override
        public BigDecimal getValueWhenSold() {
            return sharesAvailabletoSell.multiply(sellPrice);
        }

        public AssetFundingOption(Asset asset) {
            assetName = asset.symbol;
            sharesAvailabletoSell = asset.quantity;
            sellPrice = asset.currentPrice;
        }

        public AssetFundingOption(Parcel in) {
            assetName = in.readString();
            sharesAvailabletoSell = (BigDecimal) in.readSerializable();
            sellPrice = (BigDecimal) in.readSerializable();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(assetName);
            dest.writeSerializable(sharesAvailabletoSell);
            dest.writeSerializable(sellPrice);
        }

        public static final Creator<AssetFundingOption> CREATOR = new Creator<AssetFundingOption>() {
            public AssetFundingOption createFromParcel(Parcel in) {
                return new AssetFundingOption(in);
            }

            public AssetFundingOption[] newArray(int size) {
                return new AssetFundingOption[size];
            }
        };
    }
}
