package com.rubyhuntersky.peregrine.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.rubyhuntersky.peregrine.exception.NotStoredException;
import com.rubyhuntersky.peregrine.lib.oauth.model.OauthAppToken;
import com.rubyhuntersky.peregrine.lib.oauth.model.OauthToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * @author wehjin
 * @since 11/7/15.
 */

public class Storage {

    public static final String PREFKEY_ACCESS_KEY = "prefkey-access-key";
    public static final String PREFKEY_ACCESS_SECRET = "prefkey-access-secret";
    public static final String PREFKEY_ACCESS_APP_KEY = "prefkey-access-app-key";
    public static final String PREFKEY_ACCOUNT_LIST = "prefkey-account-list";
    public static final String PREFKEY_ASSETS_LISTS = "prefkey-assets-lists";
    public static final String PREFKEY_ASSIGNMENTS = "prefkey-assignments";

    private final SharedPreferences sharedPreferences;
    private final OauthAppToken oauthAppToken;
    private OauthToken oauthToken;
    private Savelet<AllAccounts> accountsList;
    private Savelet<List<AccountAssets>> accountAssetsList;
    private Savelet<Assignments> assignments;

    public Storage(Context context, String name, OauthAppToken oauthAppToken) {
        this.oauthAppToken = oauthAppToken;
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        assignments = new Savelet<>("assignments", PREFKEY_ASSIGNMENTS, new AssignmentsBuilder());
        accountsList = new Savelet<>("accounts list", PREFKEY_ACCOUNT_LIST, new AccountsListBuilder());
        accountAssetsList = new Savelet<>("assets lists", PREFKEY_ASSETS_LISTS, new AccountAssetsListBuilder());
    }


    public Observable<Assignments> streamAssignments() {
        return assignments.stream();
    }

    public void writeAssignments(Assignments assignments) {
        this.assignments.write(assignments);
    }


    public Observable<List<AccountAssets>> streamAccountAssetsList() {
        return accountAssetsList.stream();
    }

    public void writeAccountAssetsList(List<AccountAssets> accountAssetsList) {
        this.accountAssetsList.write(accountAssetsList);
    }


    public Observable<AllAccounts> streamAccountsList() {
        return accountsList.stream();
    }

    public void writeAccountList(AllAccounts allAccounts) {
        this.accountsList.write(allAccounts);
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

                    final String appKey = sharedPreferences.getString(PREFKEY_ACCESS_APP_KEY, oauthAppToken.appKey);
                    if (!appKey.equals(oauthAppToken.appKey)) {
                        eraseOauthAccessToken();
                        subscriber.onError(new NotStoredException("No access token in storage"));
                        return;
                    }

                    oauthToken =
                          new OauthToken(sharedPreferences.getString(PREFKEY_ACCESS_KEY, null), sharedPreferences.getString(PREFKEY_ACCESS_SECRET, null), oauthAppToken);
                }
                subscriber.onNext(oauthToken);
                subscriber.onCompleted();
            }
        });
    }

    public void writeOauthAccessToken(OauthToken oauthToken) {
        sharedPreferences.edit()
                         .putString(PREFKEY_ACCESS_KEY, oauthToken.key)
                         .putString(PREFKEY_ACCESS_SECRET, oauthToken.secret)
                         .putString(PREFKEY_ACCESS_APP_KEY, oauthToken.appToken.appKey)
                         .apply();
        this.oauthToken = oauthToken;
    }

    public void eraseOauthAccessToken() {
        sharedPreferences.edit()
                         .remove(PREFKEY_ACCESS_KEY)
                         .remove(PREFKEY_ACCESS_SECRET)
                         .remove(PREFKEY_ACCESS_APP_KEY)
                         .apply();
        this.oauthToken = null;
    }

    private static class AccountAssetsListBuilder implements Builder<List<AccountAssets>> {
        @Override
        public List<AccountAssets> buildFallback() {
            return new ArrayList<>();
        }

        @Override
        public List<AccountAssets> build(String jsonString) throws JSONException {
            final JSONArray jsonArray = new JSONArray(jsonString);

            List<AccountAssets> list = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(new AccountAssets(jsonObject));
            }
            return list;
        }

        @Override
        public String stringify(List<AccountAssets> object) throws JSONException {
            final JSONArray jsonArray = new JSONArray();
            int i = 0;
            for (AccountAssets assetsList : object) {
                jsonArray.put(i, assetsList.getJsonObject());
                i++;
            }
            return jsonArray.toString();
        }
    }

    private static class AssignmentsBuilder implements Builder<Assignments> {

        @Override
        public Assignments buildFallback() {
            return new Assignments();
        }

        @Override
        public Assignments build(String jsonString) throws JSONException {
            return new Assignments(jsonString);
        }

        @Override
        public String stringify(Assignments object) throws JSONException {
            JSONObject jsonObject = object.toJSONObject();
            return jsonObject.toString();
        }
    }

    private static class AccountsListBuilder implements Builder<AllAccounts> {

        @Override
        public AllAccounts buildFallback() {
            return null;
        }

        @Override
        public AllAccounts build(String jsonString) throws JSONException {
            return new AllAccounts(jsonString);
        }

        @Override
        public String stringify(AllAccounts object) throws JSONException {
            return object.toJSONObject().toString();
        }
    }

    public class Savelet<T> {
        final String TAG = Savelet.class.getSimpleName();
        private final String name;
        private BehaviorSubject<T> subject;

        public Savelet(final String name, final String preferenceKey, final Builder<T> builder) {
            this.name = name;
            this.subject = BehaviorSubject.create(readObjectOrNullFromPreferences(preferenceKey, builder));
            subject.subscribe(new Action1<T>() {
                @Override
                public void call(T object) {
                    if (object != null) {
                        try {
                            sharedPreferences.edit().putString(preferenceKey, builder.stringify(object)).apply();
                        } catch (JSONException e) {
                            Log.e(TAG, "Write preferences", e);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e(TAG, "BehaviorSubject for " + name, throwable);
                }
            });
        }

        public Observable<T> stream() {
            return subject;
        }

        public Observable<T> read() {
            return subject.first().map(new Func1<T, T>() {
                @Override
                public T call(T object) {
                    if (object == null) {
                        throw new NotStoredException(String.format("No %s in storage", name));
                    }
                    return object;
                }
            });
        }

        public void write(T object) {
            subject.onNext(object);
        }

        private T readObjectOrNullFromPreferences(String preferenceKey, Builder<T> builder) {
            try {
                final String string = sharedPreferences.getString(preferenceKey, null);
                return string == null ? builder.buildFallback() : builder.build(string);
            } catch (JSONException e) {
                Log.e(TAG, "readObjectOrNullFromPreferences", e);
                return builder.buildFallback();
            }
        }
    }

    public interface Builder<T> {
        T buildFallback();
        T build(String jsonString) throws JSONException;
        String stringify(T object) throws JSONException;
    }
}
