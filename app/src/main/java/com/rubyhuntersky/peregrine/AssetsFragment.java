package com.rubyhuntersky.peregrine;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class AssetsFragment extends BaseFragment {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        errorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                textView.setText(throwable.getLocalizedMessage());
            }
        };
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
        getStorage()
              .readAssetsLists()
              .subscribe(new Action1<List<AccountAssetsList>>() {
                  @Override
                  public void call(List<AccountAssetsList> accountAssetsLists) {
                      updateTextView(accountAssetsLists);
                  }
              }, errorAction);
    }

    private void refresh() {
        textView.setText("");
        fetchEtradeAssetsLists()
              .toList()
              .subscribe(new Action1<List<AccountAssetsList>>() {
                  @Override
                  public void call(List<AccountAssetsList> assetsLists) {
                      getStorage().writeAssetsLists(assetsLists);
                      updateTextView(assetsLists);
                  }
              }, errorAction);
    }

    private void updateTextView(List<AccountAssetsList> accountAssetsLists) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (AccountAssetsList assetsList : accountAssetsLists) {
            stringBuilder.append(assetsList).append("\n\n");
        }
        textView.setText(stringBuilder.toString());
    }

    @NonNull
    private Observable<AccountAssetsList> fetchEtradeAssetsLists() {
        return getStorage()
              .readAccountsList()
              .flatMap(new Func1<AccountsList, Observable<EtradeAccount>>() {
                  @Override
                  public Observable<EtradeAccount> call(AccountsList accountsList) {
                      return Observable.from(accountsList.accounts);
                  }
              })
              .flatMap(new Func1<EtradeAccount, Observable<JSONObject>>() {
                  @Override
                  public Observable<JSONObject> call(EtradeAccount etradeAccount) {
                      return fetchAccountPositionsResponse(etradeAccount);
                  }
              })
              .map(new Func1<JSONObject, AccountAssetsList>() {
                  @Override
                  public AccountAssetsList call(JSONObject jsonObject) {
                      try {
                          return new AccountAssetsList(jsonObject);
                      } catch (JSONException e) {
                          throw new RuntimeException(e);
                      }
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


    private Observable<JSONObject> fetchAccountPositionsResponse(final EtradeAccount etradeAccount) {
        return getStorage().readOauthAccessToken()
                           .flatMap(new Func1<OauthToken, Observable<JSONObject>>() {
                               @Override
                               public Observable<JSONObject> call(OauthToken oauthToken) {
                                   return AssetsFragment.this.fetchAccountPositionsResponse(oauthToken, etradeAccount);
                               }
                           });
    }

    @NonNull
    private Observable<JSONObject> fetchAccountPositionsResponse(OauthToken oauthToken,
          final EtradeAccount etradeAccount) {
        return getEtradeApi().fetchAccountPositionsResponse(etradeAccount.accountId, oauthToken)
                             .map(new Func1<JSONObject, JSONObject>() {
                                 @Override
                                 public JSONObject call(JSONObject jsonObject) {
                                     try {
                                         jsonObject.putOpt("accountDescription", etradeAccount.description);
                                         jsonObject.putOpt("responseArrivalTime",
                                                           DateFormat.getInstance().format(new Date()));
                                     } catch (JSONException e) {
                                         throw new RuntimeException(e);
                                     }
                                     return jsonObject;
                                 }
                             });
    }
}
