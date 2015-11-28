package com.rubyhuntersky.peregrine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author wehjin
 * @since 11/24/15.
 */

public class AccountAssets {

    private final JSONObject jsonObject;
    private final String accountDescription;
    private final String accountId;
    private final String positions;
    private final Date arrivalTime;

    public AccountAssets(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;

        final String responseAccountId = jsonObject.optString("accountId");
        this.accountId = responseAccountId.isEmpty() ? jsonObject.optString("requestAccountId") : responseAccountId;
        this.accountDescription = jsonObject.optString("accountDescription");
        Date arrivalTime;
        try {
            arrivalTime = DateFormat.getInstance().parse(jsonObject.optString("responseArrivalTime"));
        } catch (ParseException e) {
            arrivalTime = new Date();
        }
        this.arrivalTime = arrivalTime;

        final JSONArray response = jsonObject.optJSONArray("response");
        this.positions = response == null ? "[]" : response.toString(2);
    }

    @Override
    public String toString() {
        return "AccountAssets{" +
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
