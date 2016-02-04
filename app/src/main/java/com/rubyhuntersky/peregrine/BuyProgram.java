package com.rubyhuntersky.peregrine;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wehjin
 * @since 2/4/16.
 */

public class BuyProgram implements Parcelable {
    public final BigDecimal buyAmount;
    public final List<AssetPrice> prices;
    private int selectedPrice;

    public BuyProgram(BigDecimal buyAmount, List<AssetPrice> prices, int selectedPrice) {
        this.buyAmount = buyAmount;
        this.prices = prices;
        this.selectedPrice = selectedPrice;
    }

    public BuyProgram() {
        this(BigDecimal.ZERO, Collections.<AssetPrice>emptyList(), -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(buyAmount);
        dest.writeList(prices);
        dest.writeInt(selectedPrice);
    }

    public static final Creator<BuyProgram> CREATOR = new Creator<BuyProgram>() {
        public BuyProgram createFromParcel(Parcel in) {
            BigDecimal buyAmount = (BigDecimal) in.readSerializable();
            final ArrayList<AssetPrice> prices = new ArrayList<>();
            in.readList(prices, null);
            int selectedPrice = in.readInt();
            return new BuyProgram(buyAmount, prices, selectedPrice);
        }

        public BuyProgram[] newArray(int size) {
            return new BuyProgram[size];
        }
    };

    public int getSelectedPrice() {
        return selectedPrice;
    }

    public void setSelectedPrice(int newSelectedPrice) {
        selectedPrice = newSelectedPrice;
    }

    public BigDecimal getSharesCount() {
        final AssetPrice assetPrice = prices.get(selectedPrice);
        return buyAmount.divide(assetPrice.amount, Values.SCALE, BigDecimal.ROUND_HALF_UP);
    }
}
