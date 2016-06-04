package com.rubyhuntersky.peregrine.model;

import com.rubyhuntersky.peregrine.ui.UiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wehjin
 * @since 11/16/15.
 */

public class AllAccounts {
    public static final String JSONKEY_ACCOUNTS = "accounts";
    public static final String JSONKEY_ARRIVAL_DATE = "arrivalDate";

    public List<EtradeAccount> accounts;
    public Date arrivalDate;

    public AllAccounts(List<EtradeAccount> accounts, Date arrivalDate) {
        this.accounts = accounts;
        this.arrivalDate = arrivalDate;
    }

    public AllAccounts(String jsonAccountList) throws JSONException {
        accounts = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject(jsonAccountList);
        final JSONArray jsonArray = jsonObject.getJSONArray(JSONKEY_ACCOUNTS);
        for (int i = 0; i < jsonArray.length(); i++) {
            accounts.add(new EtradeAccount(jsonArray.getJSONObject(i)));
        }
        arrivalDate = new Date(jsonObject.getLong(JSONKEY_ARRIVAL_DATE));
    }

    public CharSequence getRelativeArrivalTime() {
        return UiHelper.getRelativeTimeString(arrivalDate.getTime());
    }

    public JSONObject toJSONObject() throws JSONException {
        final JSONObject accountListJsonObject = new JSONObject();
        accountListJsonObject.put(JSONKEY_ARRIVAL_DATE, arrivalDate.getTime());

        final JSONArray jsonArray = new JSONArray();
        for (EtradeAccount account : accounts) {
            try {
                jsonArray.put(account.toJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        accountListJsonObject.put(JSONKEY_ACCOUNTS, jsonArray);
        return accountListJsonObject;
    }
}
