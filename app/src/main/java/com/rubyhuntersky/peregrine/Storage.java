package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.content.SharedPreferences;

import com.rubyhuntersky.peregrine.exception.NotStoredException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class Storage {

    public static final String PREFKEY_ACCESS_KEY = "prefkey-access-key";
    public static final String PREFKEY_ACCESS_SECRET = "prefkey-access-secret";
    public static final String PREFKEY_ACCOUNT_LIST = "prefkey-account-list";
    public static final String PREFKEY_ASSETS_LISTS = "prefkey-assets-lists";

    private final SharedPreferences sharedPreferences;
    private OauthToken oauthToken;
    private Savelet<AccountsList> accountsList;
    private Savelet<List<AccountAssetsList>> assetsLists;

    public Storage(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        accountsList = new Savelet<>(PREFKEY_ACCOUNT_LIST, "accounts list", new Builder<AccountsList>() {
            @Override
            public AccountsList build(String jsonString) throws JSONException {
                return new AccountsList(jsonString);
            }

            @Override
            public String stringify(AccountsList object) throws JSONException {
                return object.toJSONObject().toString();
            }
        });
        assetsLists = new Savelet<>(PREFKEY_ASSETS_LISTS, "assets lists", new Builder<List<AccountAssetsList>>() {
            @Override
            public List<AccountAssetsList> build(String jsonString) throws JSONException {
                final JSONArray jsonArray = new JSONArray(jsonString);

                List<AccountAssetsList> list = new ArrayList<>(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(new AccountAssetsList(jsonObject));
                }
                return list;
            }

            @Override
            public String stringify(List<AccountAssetsList> object) throws JSONException {
                final JSONArray jsonArray = new JSONArray();
                int i = 0;
                for (AccountAssetsList assetsList : object) {
                    jsonArray.put(i, assetsList.getJsonObject());
                    i++;
                }
                return jsonArray.toString();
            }
        });
    }

    public void writeAssetsLists(List<AccountAssetsList> assetsLists) {
        this.assetsLists.write(assetsLists);
    }

    public Observable<List<AccountAssetsList>> readAssetsLists() {
        return assetsLists.read();
    }

    public void writeAccountList(AccountsList accountsList) {
        this.accountsList.write(accountsList);
    }

    public Observable<AccountsList> readAccountsList() {
        return accountsList.read();
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

    public <T> Observable<T> readObject(final String preferenceKey, final String name, final Builder<T> builder) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                final String string = sharedPreferences.getString(preferenceKey, null);
                if (string == null) {
                    subscriber.onError(new NotStoredException(String.format("No %s in storage", name)));
                    return;
                }
                try {
                    subscriber.onNext(builder.build(string));
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public class Savelet<T> {
        private final String preferenceKey;
        private final String name;
        private final Builder<T> builder;
        private T object;

        public Savelet(String preferenceKey, String name, final Builder<T> builder) {
            this.preferenceKey = preferenceKey;
            this.name = name;
            this.builder = new Builder<T>() {
                @Override
                public T build(String jsonString) throws JSONException {
                    return (object = builder.build(jsonString));
                }

                @Override
                public String stringify(T object) throws JSONException {
                    return builder.stringify(object);
                }
            };
        }

        public Observable<T> read() {
            return Observable.just(object)
                             .flatMap(new Func1<T, Observable<T>>() {
                                 @Override
                                 public Observable<T> call(final T object) {
                                     return object == null ? readObject(preferenceKey, name, builder)
                                           : Observable.just(object);
                                 }
                             });
        }

        public void write(T object) {
            this.object = object;
            try {
                sharedPreferences.edit()
                                 .putString(preferenceKey, builder.stringify(object))
                                 .apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface Builder<T> {
        T build(String jsonString) throws JSONException;
        String stringify(T object) throws JSONException;
    }
}
