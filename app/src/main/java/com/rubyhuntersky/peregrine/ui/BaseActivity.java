package com.rubyhuntersky.peregrine.ui;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rubyhuntersky.peregrine.AccountAssets;
import com.rubyhuntersky.peregrine.AccountsList;
import com.rubyhuntersky.peregrine.EtradeAccount;
import com.rubyhuntersky.peregrine.EtradeApi;
import com.rubyhuntersky.peregrine.OauthToken;
import com.rubyhuntersky.peregrine.OauthVerifier;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.PortfolioAssets;
import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.Storage;
import com.rubyhuntersky.peregrine.exception.NotStoredException;
import com.rubyhuntersky.peregrine.exception.ProductionStorage;

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
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

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
            showErrorDialog(title, throwable);
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
        storage = new ProductionStorage(this);

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
        for (String s = br.readLine(); s != null; s = br.readLine()) stringBuilder.append(s);
        for (int c = br.read(); c != -1; c = br.read()) stringBuilder.append((char) c);
        return stringBuilder.toString();
    }

    public Observable<PartitionList> getPartitionListStream() {
        return partitionListStream;
    }

    public Observable<AccountsList> getAccountsListStream() {
        return storage.streamAccountsList();
    }

    public Observable<PortfolioAssets> getPortfolioAssetsStream() {
        return storage.streamAccountAssetsList()
                      .map(new Func1<List<AccountAssets>, PortfolioAssets>() {
                          @Override
                          public PortfolioAssets call(List<AccountAssets> accountAssetsList) {
                              return new PortfolioAssets(accountAssetsList);
                          }
                      });
    }

    public void refresh() {
        refreshSubscription.unsubscribe();
        refreshSubscription = fetchAndStoreAccountsList()
              .flatMap(new Func1<AccountsList, Observable<List<AccountAssets>>>() {
                  @Override
                  public Observable<List<AccountAssets>> call(AccountsList accountsList) {
                      return fetchAndStoreAccountAssetsList(accountsList);
                  }
              })
              .subscribe(new Action1<List<AccountAssets>>() {
                  @Override
                  public void call(List<AccountAssets> accountAssetsList) {
                      storage.writeAccountAssetsList(accountAssetsList);
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
    private Observable<List<AccountAssets>> fetchAndStoreAccountAssetsList(AccountsList accountsList) {
        return (accountsList == null ? Observable.just((List<AccountAssets>) null)
              : fetchAccountAssets(accountsList).toList())
              .doOnNext(new Action1<List<AccountAssets>>() {
                  @Override
                  public void call(List<AccountAssets> accountAssetsList) {
                      getStorage().writeAccountAssetsList(accountAssetsList);
                  }
              });
    }

    @NonNull
    private Observable<AccountAssets> fetchAccountAssets(AccountsList accountsList) {
        return Observable.from(accountsList.accounts)
                         .flatMap(new Func1<EtradeAccount, Observable<JSONObject>>() {
                             @Override
                             public Observable<JSONObject> call(EtradeAccount etradeAccount) {
                                 return fetchAccountPositionsResponse(etradeAccount);
                             }
                         })
                         .map(new Func1<JSONObject, AccountAssets>() {
                             @Override
                             public AccountAssets call(JSONObject jsonObject) {
                                 try {
                                     return new AccountAssets(jsonObject);
                                 } catch (JSONException e) {
                                     throw new RuntimeException(e);
                                 }
                             }
                         });
    }


    protected Observable<AccountsList> fetchAndStoreAccountsList() {
        final Func1<OauthToken, Observable<List<EtradeAccount>>> accessTokenToAccountList = new Func1<OauthToken,
              Observable<List<EtradeAccount>>>() {
            @Override
            public Observable<List<EtradeAccount>> call(OauthToken oauthToken) {
                return getEtradeApi().fetchAccountList(oauthToken);
            }
        };
        return getOauthAccessToken()
              .flatMap(accessTokenToAccountList)
              .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<EtradeAccount>>>() {
                  @Override
                  public Observable<? extends List<EtradeAccount>> call(Throwable throwable) {
                      if (throwable instanceof EtradeApi.NotAuthorizedException) {
                          return renewOauthAccessToken()
                                .flatMap(accessTokenToAccountList);
                      } else {
                          return Observable.error(throwable);
                      }
                  }
              })
              .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<EtradeAccount>>>() {
                  @Override
                  public Observable<? extends List<EtradeAccount>> call(Throwable throwable) {
                      if (throwable instanceof EtradeApi.NotAuthorizedException) {
                          return fetchOauthAccessToken()
                                .flatMap(accessTokenToAccountList)
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
              .map(new Func1<List<EtradeAccount>, AccountsList>() {
                  @Override
                  public AccountsList call(List<EtradeAccount> etradeAccounts) {
                      return new AccountsList(etradeAccounts, new Date());
                  }
              })
              .doOnNext(new Action1<AccountsList>() {
                  @Override
                  public void call(AccountsList accountList) {
                      getStorage().writeAccountList(accountList);
                  }
              });
    }

    private Observable<JSONObject> fetchAccountPositionsResponse(final EtradeAccount etradeAccount) {
        return getStorage().readOauthAccessToken()
                           .flatMap(new Func1<OauthToken, Observable<JSONObject>>() {
                               @Override
                               public Observable<JSONObject> call(OauthToken oauthToken) {
                                   return fetchAccountPositionsResponse(oauthToken, etradeAccount);
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
                                         jsonObject.putOpt("requestAccountId", etradeAccount.accountId);
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
        return getOauthAccessToken()
              .flatMap(new Func1<OauthToken, Observable<OauthToken>>() {
                  @Override
                  public Observable<OauthToken> call(OauthToken oauthToken) {
                      return getEtradeApi().renewOauthAccessToken(oauthToken);
                  }
              });
    }

    private Observable<OauthToken> fetchOauthAccessToken() {
        return getEtradeApi().fetchOauthRequestToken()
                             .flatMap(new Func1<OauthToken, Observable<OauthVerifier>>() {
                                 @Override
                                 public Observable<OauthVerifier> call(OauthToken requestToken) {
                                     return promptForVerifier(requestToken);
                                 }
                             })
                             .flatMap(new Func1<OauthVerifier, Observable<OauthToken>>() {
                                 @Override
                                 public Observable<OauthToken> call(OauthVerifier verifier) {
                                     return getEtradeApi().fetchOauthAccessToken(verifier);
                                 }
                             })
                             .doOnNext(new Action1<OauthToken>() {
                                 @Override
                                 public void call(OauthToken oauthToken) {
                                     getStorage().writeOauthAccessToken(oauthToken);
                                 }
                             });
    }

    private Observable<OauthVerifier> promptForVerifier(final OauthToken oauthRequestToken) {
        return Observable.create(new Observable.OnSubscribe<OauthVerifier>() {
            @Override
            public void call(final Subscriber<? super OauthVerifier> subscriber) {
                final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                final VerifierFragment verifierFragment = VerifierFragment.newInstance(
                      getEtradeApi().oauthAppToken.appKey,
                      oauthRequestToken.key);

                verifierFragment.setListener(new VerifierFragment.Listener() {

                    @Override
                    public void onVerifier(String verifier) {
                        fragmentManager.popBackStack();
                        subscriber.onNext(new OauthVerifier(verifier, oauthRequestToken));
                        subscriber.onCompleted();
                    }
                });
                fragmentManager.beginTransaction()
                               .add(R.id.verifier_frame, verifierFragment, "VerifierFragment")
                               .addToBackStack(null)
                               .commit();
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        if (fragmentManager.findFragmentByTag("VerifierFragment") == null) {
                            return;
                        }
                        fragmentManager.popBackStack();
                    }
                }));
            }
        });
    }

    protected void showErrorDialog(String title, Throwable throwable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(throwable.toString())
               .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .show();
    }
}
