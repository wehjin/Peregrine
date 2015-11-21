package com.rubyhuntersky.peregrine;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class AssetsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final StringBuilder stringBuilder = new StringBuilder();
        storage.readAccountList().flatMap(new Func1<EtradeAccountList, Observable<JSONObject>>() {
            @Override
            public Observable<JSONObject> call(EtradeAccountList accountList) {
                return fetchAccountPositionsResponses(accountList);
            }
        }).subscribe(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                try {
                    final String accountDescription = jsonObject.optString("accountDescription");
                    final String accountId = jsonObject.optString("accountId");
                    final String positions = jsonObject.optJSONArray("response").toString(2);
                    stringBuilder.append(accountDescription).append("\n")
                                 .append(accountId).append("\n")
                                 .append(positions).append("\n\n");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, errorAction, new Action0() {
            @Override
            public void call() {
                final TextView textView = (TextView) findViewById(R.id.text);
                textView.setText(stringBuilder.toString());
            }
        });
    }

    private Observable<JSONObject> fetchAccountPositionsResponses(EtradeAccountList accountList) {
        return Observable.from(accountList.accounts)
                         .flatMap(new Func1<EtradeAccount, Observable<JSONObject>>() {
                             @Override
                             public Observable<JSONObject> call(final EtradeAccount etradeAccount) {
                                 return fetchAccountPositionsResponse(etradeAccount);
                             }
                         });
    }

    private Observable<JSONObject> fetchAccountPositionsResponse(final EtradeAccount etradeAccount) {
        return storage.readOauthAccessToken()
                      .flatMap(new Func1<OauthToken, Observable<JSONObject>>() {
                          @Override
                          public Observable<JSONObject> call(OauthToken oauthToken) {
                              return etradeApi.fetchAccountPositionsResponse(
                                    etradeAccount.accountId, oauthToken).map(new Func1<JSONObject, JSONObject>() {
                                  @Override
                                  public JSONObject call(JSONObject jsonObject) {
                                      try {
                                          jsonObject.putOpt("accountDescription", etradeAccount.description);
                                      } catch (JSONException e) {
                                          throw new RuntimeException(e);
                                      }
                                      return jsonObject;
                                  }
                              });
                          }
                      });
    }
}
