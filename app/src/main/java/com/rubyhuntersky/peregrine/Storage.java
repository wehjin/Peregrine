package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.content.SharedPreferences;

import com.rubyhuntersky.peregrine.exception.NotStoredException;

import rx.Observable;
import rx.Subscriber;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class Storage {

    public static final String PREFKEY_ACCESS_KEY = "prefkey-access-key";
    public static final String PREFKEY_ACCESS_SECRET = "prefkey-access-secret";
    private final SharedPreferences sharedPreferences;
    private OauthToken oauthToken;

    public Storage(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
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
