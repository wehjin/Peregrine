package com.rubyhuntersky.peregrine;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class EtradeAccount {

    public static final String JSONKEY_DESCRIPTION = "description";
    public static final String JSONKEY_ACCOUNT_ID = "accountId";
    public static final String JSONKEY_REGISTRATION_TYPE = "registrationType";
    public static final String JSONKEY_NET_ACCOUNT_VALUE = "netAccountValue";
    public String description;
    public String accountId;
    public String registrationType;
    public BigDecimal netAccountValue;

    public BigDecimal getNetAccountValue() {
        return netAccountValue;
    }

    public FundingAccount toFundingAccount(final AccountBalance accountBalance, List<Asset> assets) {
        return new EtradeFundingAccount(this, accountBalance, assets);
    }

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKEY_DESCRIPTION, this.description);
        jsonObject.put(JSONKEY_ACCOUNT_ID, this.accountId);
        jsonObject.put(JSONKEY_REGISTRATION_TYPE, this.registrationType);
        jsonObject.put(JSONKEY_NET_ACCOUNT_VALUE, this.netAccountValue.toString());
        return jsonObject;
    }

    @Override
    public String toString() {
        return "EtradeAccount{" +
              "description='" + description + '\'' +
              ", accountId='" + accountId + '\'' +
              ", registrationType='" + registrationType + '\'' +
              ", netAccountValue=" + netAccountValue +
              '}';
    }

    public EtradeAccount(String description, String accountId, String registrationType, BigDecimal netAccountValue) {
        this.description = description;
        this.accountId = accountId;
        this.registrationType = registrationType;
        this.netAccountValue = netAccountValue;
    }

    public EtradeAccount(Element accountElement) {
        final NodeList childNodes = accountElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) item;
            final String tagName = element.getTagName();
            if ("accountDesc".equals(tagName)) {
                description = element.getTextContent();
            } else if ("netAccountValue".equals(tagName)) {
                netAccountValue = new BigDecimal(element.getTextContent());
            } else if ("accountId".equals(tagName)) {
                accountId = element.getTextContent();
            } else if ("registrationType".equals(tagName)) {
                registrationType = element.getTextContent();
            }
        }
    }

    public EtradeAccount(JSONObject jsonObject) throws JSONException {
        description = jsonObject.getString(JSONKEY_DESCRIPTION);
        accountId = jsonObject.getString(JSONKEY_ACCOUNT_ID);
        registrationType = jsonObject.getString(JSONKEY_REGISTRATION_TYPE);
        netAccountValue = new BigDecimal(jsonObject.getString(JSONKEY_NET_ACCOUNT_VALUE));
    }

    private static class EtradeFundingAccount implements FundingAccount {

        final private List<FundingOption> fundingOptions;
        final private BigDecimal cashAvailable;
        final private String accountName;

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

        public EtradeFundingAccount(EtradeAccount etradeAccount, AccountBalance accountBalance, List<Asset> assets) {
            accountName = etradeAccount.accountId;
            cashAvailable = accountBalance.netCash;
            fundingOptions = new ArrayList<>();
            for (Asset asset : assets) {
                fundingOptions.add(asset.toFundingOption());
            }
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
}
