package com.rubyhuntersky.peregrine.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;

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

}
