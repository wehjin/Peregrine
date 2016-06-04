package com.rubyhuntersky.peregrine.model;

import android.os.Parcel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 2/6/16.
 */
class EtradeFundingAccount implements FundingAccount {

    final private List<FundingOption> fundingOptions;
    final private BigDecimal cashAvailable;
    final private String accountName;

    public EtradeFundingAccount(String accountId, List<Asset> assets) {
        accountName = accountId;
        BigDecimal cashAvailable = BigDecimal.ZERO;
        List<FundingOption> fundingOptions = new ArrayList<>();
        for (Asset asset : assets) {
            if (asset.symbol.equals(Values.USD)) {
                cashAvailable = asset.marketValue;
            } else {
                fundingOptions.add(asset.toFundingOption());
            }
        }
        this.cashAvailable = cashAvailable;
        this.fundingOptions = fundingOptions;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public BigDecimal getCashAvailable() {
        return cashAvailable;
    }

    @Override
    public List<FundingOption> getFundingOptions(String exclude) {
        final ArrayList<FundingOption> includedOptions = new ArrayList<>();
        for (FundingOption option : fundingOptions) {
            if (option.getAssetName().equals(exclude)) {
                continue;
            }
            includedOptions.add(option);
        }
        return includedOptions;
    }

    public EtradeFundingAccount(Parcel in) {
        accountName = in.readString();
        cashAvailable = (BigDecimal) in.readSerializable();
        fundingOptions = new ArrayList<>();
        in.readList(fundingOptions, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountName);
        dest.writeSerializable(cashAvailable);
        dest.writeList(fundingOptions);
    }

    public static final Creator<EtradeFundingAccount> CREATOR = new Creator<EtradeFundingAccount>() {
        public EtradeFundingAccount createFromParcel(Parcel in) {
            return new EtradeFundingAccount(in);
        }

        public EtradeFundingAccount[] newArray(int size) {
            return new EtradeFundingAccount[size];
        }
    };
}
