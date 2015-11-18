package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.content.SharedPreferences;

import com.rubyhuntersky.peregrine.exception.NotStoredException;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class Storage {

    public static final String PREFKEY_ACCESS_KEY = "prefkey-access-key";
    public static final String PREFKEY_ACCESS_SECRET = "prefkey-access-secret";
    public static final String PREFKEY_ACCOUNT_LIST = "prefkey-account-list";
    private final SharedPreferences sharedPreferences;
    private OauthToken oauthToken;
    private EtradeAccountList etradeAccounts;

    public Storage(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void writeAccountList(EtradeAccountList accountList) {
        this.etradeAccounts = accountList;

        final JSONObject jsonObject;
        try {
            jsonObject = accountList.toJSONObject();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        sharedPreferences.edit()
                         .putString(PREFKEY_ACCOUNT_LIST, jsonObject.toString())
                         .apply();
    }

    public Observable<EtradeAccountList> readAccountList() {
        return Observable.create(new Observable.OnSubscribe<EtradeAccountList>() {
            @Override
            public void call(Subscriber<? super EtradeAccountList> subscriber) {
                if (etradeAccounts == null) {
                    if (!sharedPreferences.contains(PREFKEY_ACCOUNT_LIST)) {
                        subscriber.onError(new NotStoredException("No account list in storage"));
                        return;
                    }
                    try {
                        final String jsonAccountList = sharedPreferences.getString(PREFKEY_ACCOUNT_LIST, "[]");
                        etradeAccounts = new EtradeAccountList(jsonAccountList);
                    } catch (JSONException e) {
                        subscriber.onError(e);
                        return;
                    }
                }
                subscriber.onNext(etradeAccounts);
                subscriber.onCompleted();
            }
        });
    }

    public void writeOauthAccessToken(OauthToken oauthToken) {
        sharedPreferences.edit()
                         .putString(PREFKEY_ACCESS_KEY, oauthToken.key)
                         .putString(PREFKEY_ACCESS_SECRET, oauthToken.secret)
                         .apply();
        this.oauthToken = oauthToken;
    }

    public Observable<OauthToken> readOauthAccessToken() {
        return Observable.create(new Observable.OnSubscribe<OauthToken>() {
            @Override
            public void call(Subscriber<? super OauthToken> subscriber) {
                if (oauthToken == null) {
                    if (!sharedPreferences.contains(PREFKEY_ACCESS_KEY)) {
                        subscriber.onError(new NotStoredException("No access token in storage"));
                        return;
                    }
                    oauthToken = new OauthToken(sharedPreferences.getString(PREFKEY_ACCESS_KEY, null),
                                                sharedPreferences.getString(PREFKEY_ACCESS_SECRET, null));
                }
                subscriber.onNext(oauthToken);
                subscriber.onCompleted();
            }
        });
    }

    public void eraseOauthAccessToken() {
        sharedPreferences.edit()
                         .remove(PREFKEY_ACCESS_KEY)
                         .remove(PREFKEY_ACCESS_SECRET)
                         .apply();
        this.oauthToken = null;
    }
}
