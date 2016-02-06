package com.rubyhuntersky.peregrine;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 1/29/16.
 */
public class AssetPrice implements Parcelable {
    public final String name;
    public final BigDecimal price;

    public AssetPrice(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public AssetPrice(Parcel in) {
        this(in.readString(), (BigDecimal) in.readSerializable());
    }

    public AssetPrice() {
        this("-", BigDecimal.ONE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(price);
    }

    public static final Creator<AssetPrice> CREATOR = new Creator<AssetPrice>() {
        public AssetPrice createFromParcel(Parcel in) {
            return new AssetPrice(in);
        }

        public AssetPrice[] newArray(int size) {
            return new AssetPrice[size];
        }
    };
}
