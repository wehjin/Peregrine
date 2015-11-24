package com.rubyhuntersky.peregrine;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author wehjin
 * @since 11/24/15.
 */

public class AccountAssetsList {

    private final JSONObject jsonObject;
    private final String accountDescription;
    private final String accountId;
    private final String positions;
    private final Date arrivalTime;

    public AccountAssetsList(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;
        this.accountDescription = jsonObject.optString("accountDescription");
        this.accountId = jsonObject.optString("accountId");
        Date arrivalTime;
        try {
            arrivalTime = DateFormat.getInstance().parse(jsonObject.optString("responseArrivalTime"));
        } catch (ParseException e) {
            arrivalTime = new Date();
        }
        this.arrivalTime = arrivalTime;
        this.positions = jsonObject.optJSONArray("response").toString(2);
    }

    @Override
    public String toString() {
        return "AccountAssetsList{" +
              "accountDescription='" + accountDescription + '\'' +
              ", accountId='" + accountId + '\'' +
              ", positions='" + positions + '\'' +
              ", arrivalTime=" + arrivalTime +
              '}';
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
