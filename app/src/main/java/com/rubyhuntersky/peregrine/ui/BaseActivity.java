package com.rubyhuntersky.peregrine.ui;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

import com.rubyhuntersky.peregrine.AccountAssets;
import com.rubyhuntersky.peregrine.AllAccounts;
import com.rubyhuntersky.peregrine.EtradeAccount;
import com.rubyhuntersky.peregrine.EtradeApi;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.PortfolioAssets;
import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.Storage;
import com.rubyhuntersky.peregrine.exception.NotStoredException;
import com.rubyhuntersky.peregrine.exception.ProductionStorage;
import com.rubyhuntersky.peregrine.oauth.OauthToken;
import com.rubyhuntersky.peregrine.oauth.OauthVerifier;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

import static com.rubyhuntersky.peregrine.ui.oauth.OauthUi.promptForVerifier;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected Action1<Throwable> errorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            final String title = "ERROR";
            Log.e(TAG, title, throwable);
            alertError(title, throwable);
        }
    };
    private Subscription refreshSubscription = Subscriptions.empty();
    private EtradeApi etradeApi;
    private Storage storage;
    private BehaviorSubject<PartitionList> partitionListStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etradeApi = new EtradeApi(this);
        storage = new ProductionStorage(this, etradeApi.oauthAppToken);

        try {
            final InputStream inputStream = getResources().openRawResource(R.raw.starting_partitions);
            partitionListStream = BehaviorSubject.create(new PartitionList(new JSONObject(getString(inputStream))));
        } catch (JSONException | IOException e) {
            Log.e(TAG, "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private String getString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        for (String s = br.readLine(); s != null; s = br.readLine())
            stringBuilder.append(s);
        for (int c = br.read(); c != -1; c = br.read())
            stringBuilder.append((char) c);
        return stringBuilder.toString();
    }

    public Observable<PartitionList> getPartitionListStream() {
        return partitionListStream;
    }

    public Observable<AllAccounts> getAllAccountsStream() {
        return storage.streamAccountsList();
    }


    public Observable<List<AccountAssets>> getAccountAssetsListStream() {
        return storage.streamAccountAssetsList();
    }

    public Observable<PortfolioAssets> getPortfolioAssetsStream() {
        return storage.streamAccountAssetsList().map(new Func1<List<AccountAssets>, PortfolioAssets>() {
            @Override
            public PortfolioAssets call(List<AccountAssets> accountAssetsList) {
                return new PortfolioAssets(accountAssetsList);
            }
        });
    }

    public void refresh() {
        refreshSubscription.unsubscribe();
        refreshSubscription =
              fetchAndStoreAccountsList().flatMap(new Func1<AllAccounts, Observable<List<AccountAssets>>>() {
                  @Override
                  public Observable<List<AccountAssets>> call(AllAccounts allAccounts) {
                      return fetchAndStoreAccountAssetsList(allAccounts);
                  }
              }).subscribe(new Action1<List<AccountAssets>>() {
                  @Override
                  public void call(List<AccountAssets> accountAssetList) {
                      logDebug("Refresh completed");
                  }
              }, new Action1<Throwable>() {
                  @Override
                  public void call(Throwable throwable) {
                      alertError("Refresh error", throwable);
                  }
              });
    }

    public EtradeApi getEtradeApi() {
        return etradeApi;
    }

    public Storage getStorage() {
        return storage;
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @NonNull
    private Observable<List<AccountAssets>> fetchAndStoreAccountAssetsList(AllAccounts allAccounts) {
        return (allAccounts == null
                ? Observable.just((List<AccountAssets>) null)
                : fetchAccountAssets(allAccounts).toList()).doOnNext(new Action1<List<AccountAssets>>() {
            @Override
            public void call(List<AccountAssets> accountAssetsList) {
                getStorage().writeAccountAssetsList(accountAssetsList);
            }
        });
    }

    @NonNull
    private Observable<AccountAssets> fetchAccountAssets(AllAccounts allAccounts) {
        return Observable.from(allAccounts.accounts)
                         .flatMap(new Func1<EtradeAccount, Observable<Pair<JSONObject, JSONObject>>>() {
                             @Override
                             public Observable<Pair<JSONObject, JSONObject>> call(EtradeAccount etradeAccount) {
                                 return fetchAccountDetails(etradeAccount);
                             }
                         })
                         .map(new Func1<Pair<JSONObject, JSONObject>, AccountAssets>() {
                             @Override
                             public AccountAssets call(Pair<JSONObject, JSONObject> jsonPair) {
                                 try {
                                     return new AccountAssets(EtradeApi.addBalanceToPositions(jsonPair.first, jsonPair.second));
                                 } catch (JSONException e) {
                                     throw new RuntimeException(e);
                                 }
                             }
                         });
    }


    protected Observable<AllAccounts> fetchAndStoreAccountsList() {
        final Func1<OauthToken, Observable<List<EtradeAccount>>> accessTokenToAccountList =
              new Func1<OauthToken, Observable<List<EtradeAccount>>>() {
                  @Override
                  public Observable<List<EtradeAccount>> call(OauthToken oauthToken) {
                      return getEtradeApi().fetchAccountList(oauthToken);
                  }
              };
        return getOauthAccessToken().flatMap(accessTokenToAccountList)
                                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<EtradeAccount>>>() {
                                        @Override
                                        public Observable<? extends List<EtradeAccount>> call(Throwable throwable) {
                                            if (throwable instanceof EtradeApi.NotAuthorizedException) {
                                                return renewOauthAccessToken().flatMap(accessTokenToAccountList);
                                            } else {
                                                return Observable.error(throwable);
                                            }
                                        }
                                    })
                                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<EtradeAccount>>>() {
                                        @Override
                                        public Observable<? extends List<EtradeAccount>> call(Throwable throwable) {
                                            if (throwable instanceof EtradeApi.NotAuthorizedException) {
                                                return fetchOauthAccessToken().flatMap(accessTokenToAccountList)
                                                                              .doOnSubscribe(new Action0() {
                                                                                  @Override
                                                                                  public void call() {
                                                                                      getStorage().eraseOauthAccessToken();
                                                                                  }
                                                                              });
                                            } else {
                                                return Observable.error(throwable);
                                            }
                                        }
                                    })
                                    .map(new Func1<List<EtradeAccount>, AllAccounts>() {
                                        @Override
                                        public AllAccounts call(List<EtradeAccount> etradeAccounts) {
                                            return new AllAccounts(etradeAccounts, new Date());
                                        }
                                    })
                                    .doOnNext(new Action1<AllAccounts>() {
                                        @Override
                                        public void call(AllAccounts allAccounts) {
                                            getStorage().writeAccountList(allAccounts);
                                        }
                                    });
    }

    private Observable<Pair<JSONObject, JSONObject>> fetchAccountDetails(final EtradeAccount etradeAccount) {
        return getStorage().readOauthAccessToken()
                           .flatMap(new Func1<OauthToken, Observable<Pair<JSONObject, JSONObject>>>() {
                               @Override
                               public Observable<Pair<JSONObject, JSONObject>> call(OauthToken oauthToken) {
                                   return fetchAccountPositionsAndBalanceResponses(oauthToken, etradeAccount);
                               }
                           });
    }

    @NonNull
    private Observable<Pair<JSONObject, JSONObject>> fetchAccountPositionsAndBalanceResponses(OauthToken oauthToken, EtradeAccount etradeAccount) {
        final Func2<JSONObject, JSONObject, Pair<JSONObject, JSONObject>> toPair =
              new Func2<JSONObject, JSONObject, Pair<JSONObject, JSONObject>>() {
                  @Override
                  public Pair<JSONObject, JSONObject> call(JSONObject positions, JSONObject balance) {
                      return new Pair<>(positions, balance);
                  }
              };
        return Observable.zip(fetchAccountPositionsResponse(oauthToken, etradeAccount), fetchAccountBalanceResponse(oauthToken, etradeAccount), toPair);
    }

    @NonNull
    private Observable<JSONObject> fetchAccountPositionsResponse(OauthToken oauthToken, final EtradeAccount etradeAccount) {
        return getEtradeApi().fetchAccountPositionsResponse(etradeAccount.accountId, oauthToken)
                             .retry(1)
                             .map(new Func1<JSONObject, JSONObject>() {
                                 @Override
                                 public JSONObject call(JSONObject jsonObject) {
                                     try {
                                         jsonObject.putOpt("accountDescription", etradeAccount.description);
                                         jsonObject.putOpt("requestAccountId", etradeAccount.accountId);
                                         jsonObject.putOpt("responseArrivalTime", DateFormat.getInstance()
                                                                                            .format(new Date()));
                                     } catch (JSONException e) {
                                         throw new RuntimeException(e);
                                     }
                                     return jsonObject;
                                 }
                             });
    }

    @NonNull
    private Observable<JSONObject> fetchAccountBalanceResponse(OauthToken oauthToken, final EtradeAccount etradeAccount) {
        return getEtradeApi().fetchAccountBalanceResponse(etradeAccount.accountId, oauthToken)
                             .retry(1)
                             .map(new Func1<JSONObject, JSONObject>() {
                                 @Override
                                 public JSONObject call(JSONObject jsonObject) {
                                     try {
                                         jsonObject.putOpt("accountDescription", etradeAccount.description);
                                         jsonObject.putOpt("requestAccountId", etradeAccount.accountId);
                                         jsonObject.putOpt("responseArrivalTime", DateFormat.getInstance()
                                                                                            .format(new Date()));
                                     } catch (JSONException e) {
                                         throw new RuntimeException(e);
                                     }
                                     return jsonObject;
                                 }
                             })
                             .doOnError(new Action1<Throwable>() {
                                 @Override
                                 public void call(Throwable throwable) {
                                     Log.e(TAG, " v error, account: " + etradeAccount);
                                 }
                             });
    }


    private Observable<OauthToken> getOauthAccessToken() {
        return getStorage().readOauthAccessToken()
                           .onErrorResumeNext(new Func1<Throwable, Observable<? extends OauthToken>>() {
                               @Override
                               public Observable<? extends OauthToken> call(Throwable throwable) {
                                   if (throwable instanceof NotStoredException) {
                                       return fetchOauthAccessToken();
                                   } else {
                                       return Observable.error(throwable);
                                   }
                               }
                           });
    }

    private Observable<OauthToken> renewOauthAccessToken() {
        return getOauthAccessToken().flatMap(new Func1<OauthToken, Observable<OauthToken>>() {
            @Override
            public Observable<OauthToken> call(OauthToken oauthToken) {
                return getEtradeApi().renewOauthAccessToken(oauthToken);
            }
        });
    }

    private Observable<OauthToken> fetchOauthAccessToken() {
        return getEtradeApi().fetchOauthRequestToken().flatMap(new Func1<OauthToken, Observable<OauthVerifier>>() {
            @Override
            public Observable<OauthVerifier> call(OauthToken requestToken) {
                return promptForVerifier(BaseActivity.this, requestToken);
            }
        }).flatMap(new Func1<OauthVerifier, Observable<OauthToken>>() {
            @Override
            public Observable<OauthToken> call(OauthVerifier verifier) {
                return getEtradeApi().fetchOauthAccessToken(verifier);
            }
        }).doOnNext(new Action1<OauthToken>() {
            @Override
            public void call(OauthToken oauthToken) {
                getStorage().writeOauthAccessToken(oauthToken);
            }
        });
    }

    protected void alertError(String title, Throwable throwable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
               .setMessage(throwable.toString())
               .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .show();
        logError(title, throwable);
    }

    protected void logDebug(String message) {
        Log.d(TAG, message);
    }

    protected void logError(String message, Throwable throwable) {
        Log.e(TAG, message + "\n" + throwable.getLocalizedMessage());
    }
}
