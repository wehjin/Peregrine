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

public class BuyProgram implements Parcelable, FundingProgram {
    public final BigDecimal buyAmount;
    public final List<AssetPrice> buyOptions;
    private int selectedBuyOption;
    private List<? extends FundingAccount> fundingAccounts = Collections.emptyList();
    private int selectedFundingAccount;
    private int selectedFundingOption;

    public BuyProgram(BigDecimal buyAmount, List<AssetPrice> buyOptions, int selectedBuyOption) {
        this.buyAmount = buyAmount;
        this.buyOptions = buyOptions;
        this.selectedBuyOption = selectedBuyOption;
    }

    public BuyProgram() {
        this(BigDecimal.ZERO, Collections.<AssetPrice>emptyList(), -1);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(buyAmount);
        dest.writeList(buyOptions);
        dest.writeInt(selectedBuyOption);
        dest.writeList(fundingAccounts);
        dest.writeInt(selectedFundingAccount);
        dest.writeInt(selectedFundingOption);
    }

    public static final Creator<BuyProgram> CREATOR = new Creator<BuyProgram>() {
        public BuyProgram createFromParcel(Parcel in) {
            BigDecimal buyAmount = (BigDecimal) in.readSerializable();

            final ArrayList<AssetPrice> prices = new ArrayList<>();
            in.readList(prices, null);
            int selectedTradingAsset = in.readInt();
            final ArrayList<FundingAccount> fundingAccounts = new ArrayList<>();
            in.readList(fundingAccounts, null);
            final int selectedFundingAccount = in.readInt();
            final int selectedFundingOption = in.readInt();

            final BuyProgram buyProgram = new BuyProgram(buyAmount, prices, selectedTradingAsset);
            buyProgram.setFundingAccounts(fundingAccounts, 0, -1);
            buyProgram.setSelectedFundingAccount(selectedFundingAccount);
            buyProgram.setSelectedFundingOption(selectedFundingOption);
            return buyProgram;
        }

        public BuyProgram[] newArray(int size) {
            return new BuyProgram[size];
        }
    };

    public int getSelectedBuyOption() {
        return selectedBuyOption;
    }

    public void setSelectedBuyOption(int newSelectedPrice) {
        selectedBuyOption = newSelectedPrice;
    }

    public BigDecimal getSharesToBuy() {
        final AssetPrice assetPrice = getBuyOption();
        if (assetPrice == null)
            return null;
        return buyAmount.divide(assetPrice.price, Values.SCALE, BigDecimal.ROUND_HALF_UP);
    }

    public void setFundingAccounts(List<? extends FundingAccount> fundingAccounts, int selectedFundingAccount, int selectedFundingOption) {
        this.fundingAccounts = fundingAccounts;
        this.selectedFundingAccount = selectedFundingAccount;
    }

    @Override
    public List<? extends FundingAccount> getFundingAccounts() {
        return fundingAccounts;
    }

    @Override
    public int getSelectedFundingAccount() {
        return selectedFundingAccount;
    }

    @Override
    public void setSelectedFundingAccount(int selection) {
        selectedFundingAccount = selection;
    }

    @Override
    public boolean areAdditionalFundsNeededToBuy() {
        return getAdditionalFundsNeededToBuy().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getAdditionalFundsNeededToBuy() {
        final FundingAccount fundingAccount = getFundingAccount();
        if (fundingAccount == null)
            return null;
        final BigDecimal cashAvailableInFundingAccount = fundingAccount.getCashAvailable();
        return buyAmount.subtract(cashAvailableInFundingAccount).max(BigDecimal.ZERO);
    }

    @Override
    public List<FundingOption> getFundingOptions() {
        final AssetPrice buyOption = getBuyOption();
        final FundingAccount fundingAccount = getFundingAccount();
        if (fundingAccount == null || buyOption == null)
            return Collections.emptyList();
        return fundingAccount.getFundingOptions(buyOption.name);
    }

    @Override
    public int getSelectedFundingOption() {
        return selectedFundingOption;
    }

    @Override
    public void setSelectedFundingOption(int selection) {
        selectedFundingOption = selection;
    }

    @Override
    public BigDecimal getSharesToSellForFunding() {
        final FundingOption fundingOption = getFundingOptions().get(selectedBuyOption);
        final BigDecimal fundsNeeded = getAdditionalFundsNeededToBuy();
        final BigDecimal sharesToSell =
              fundsNeeded.divide(fundingOption.getSellPrice(), Values.SCALE, BigDecimal.ROUND_HALF_UP);
        return sharesToSell.min(fundingOption.getSharesAvailableToSell()).setScale(0, BigDecimal.ROUND_CEILING);
    }

    @Override
    public BigDecimal getAdditionalFundsNeededAfterSale() {
        final FundingOption fundingOption = getFundingOptions().get(selectedBuyOption);
        final BigDecimal fundedAmount = getSharesToSellForFunding().multiply(fundingOption.getSellPrice());
        return buyAmount.subtract(fundedAmount).max(BigDecimal.ZERO);
    }

    private AssetPrice getBuyOption() {
        if (selectedBuyOption < 0)
            return null;
        return buyOptions.get(selectedBuyOption);
    }

    private FundingAccount getFundingAccount() {
        if (selectedFundingAccount < 0)
            return null;
        return fundingAccounts.get(selectedFundingAccount);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
