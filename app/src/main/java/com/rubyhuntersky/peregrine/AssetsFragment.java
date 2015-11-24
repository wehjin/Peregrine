package com.rubyhuntersky.peregrine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class AssetsFragment extends BaseFragment {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_assets, container, false);
        textView = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        textView.setText("");
        final StringBuilder stringBuilder = new StringBuilder();
        getStorage().readAccountList().flatMap(new Func1<EtradeAccountList, Observable<JSONObject>>() {
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
                textView.setText(stringBuilder.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assets, menu);
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
        return getStorage().readOauthAccessToken()
                           .flatMap(new Func1<OauthToken, Observable<JSONObject>>() {
                               @Override
                               public Observable<JSONObject> call(OauthToken oauthToken) {
                                   return getEtradeApi().fetchAccountPositionsResponse(
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
