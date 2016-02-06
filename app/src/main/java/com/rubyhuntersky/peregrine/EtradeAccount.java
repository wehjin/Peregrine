package com.rubyhuntersky.peregrine;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class EtradeAccount implements FundingAccount {

    public static final String JSONKEY_DESCRIPTION = "description";
    public static final String JSONKEY_ACCOUNT_ID = "accountId";
    public static final String JSONKEY_REGISTRATION_TYPE = "registrationType";
    public static final String JSONKEY_NET_ACCOUNT_VALUE = "netAccountValue";
    public String description;
    public String accountId;
    public String registrationType;
    public BigDecimal netAccountValue;

    public EtradeAccount(String description, String accountId, String registrationType, BigDecimal netAccountValue) {
        this.description = description;
        this.accountId = accountId;
        this.registrationType = registrationType;
        this.netAccountValue = netAccountValue;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(accountId);
        dest.writeString(registrationType);
        dest.writeValue(netAccountValue);
    }

    public static final Creator<EtradeAccount> CREATOR = new Creator<EtradeAccount>() {
        public EtradeAccount createFromParcel(Parcel in) {
            String description = in.readString();
            String accountId = in.readString();
            String registrationType = in.readString();
            BigDecimal netAccountValue = (BigDecimal) in.readValue(null);
            return new EtradeAccount(description, accountId, registrationType, netAccountValue);
        }

        @Override
        public EtradeAccount[] newArray(int size) {
            return new EtradeAccount[size];
        }
    };


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

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKEY_DESCRIPTION, this.description);
        jsonObject.put(JSONKEY_ACCOUNT_ID, this.accountId);
        jsonObject.put(JSONKEY_REGISTRATION_TYPE, this.registrationType);
        jsonObject.put(JSONKEY_NET_ACCOUNT_VALUE, this.netAccountValue.toString());
        return jsonObject;
    }

    public BigDecimal getNetAccountValue() {
        return netAccountValue;
    }

    @Override
    public String getAccountName() {
        return description;
    }

    @Override
    public BigDecimal getCashAvailable() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<FundingOption> getFundingOptions(String exclude) {
        return null;
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

    @Override
    public int describeContents() {
        return 0;
    }
}
