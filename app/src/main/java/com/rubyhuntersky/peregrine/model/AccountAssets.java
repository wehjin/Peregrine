package com.rubyhuntersky.peregrine.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wehjin
 * @since 11/24/15.
 */

public class AccountAssets {

    public static final String TAG = AccountAssets.class.getSimpleName();
    private final JSONObject jsonObject;
    private final String accountId;
    private final String accountDescription;
    private final Date arrivalTime;
    private List<Asset> assets;

    public AccountAssets(String accountId, BigDecimal cashAmount, List<Asset> assets) {
        jsonObject = new JSONObject();
        this.accountId = accountId;
        this.accountDescription = "";
        this.arrivalTime = new Date();
        this.assets = new ArrayList<>(assets);
        this.assets.add(new Asset(accountId, Values.USD, cashAmount, cashAmount, BigDecimal.ONE));
    }

    public AccountAssets(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;

        this.accountId = jsonObject.optString("requestAccountId");
        this.accountDescription = jsonObject.optString("accountDescription");
        Date arrivalTime;
        try {
            arrivalTime = DateFormat.getInstance().parse(jsonObject.optString("responseArrivalTime"));
        } catch (ParseException e) {
            arrivalTime = new Date();
        }
        this.arrivalTime = arrivalTime;

        final JSONArray response = jsonObject.optJSONArray("response");
        String positions = response == null ? "[]" : response.toString(2);
        assets = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(positions);
            for (int i = 0; i < jsonArray.length(); i++) {
                assets.add(new Asset(jsonArray.getJSONObject(i), accountId, accountDescription, this.arrivalTime));
            }
        } catch (JSONException e) {
            Log.e(TAG, "toAssetList", e);
        }
    }

    @Override
    public String toString() {
        return "AccountAssets{" +
              "jsonObject=" + jsonObject +
              ", accountDescription='" + accountDescription + '\'' +
              ", accountId='" + accountId + '\'' +
              ", arrivalTime=" + arrivalTime +
              ", assets=" + assets +
              '}';
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public List<Asset> toAssetList() {
        return assets;
    }

    public FundingAccount toFundingAccount() {
        return new EtradeFundingAccount(accountId, assets);
    }
}
