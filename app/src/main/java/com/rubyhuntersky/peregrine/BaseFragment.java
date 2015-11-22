package com.rubyhuntersky.peregrine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.rubyhuntersky.peregrine.exception.NotStoredException;
import com.rubyhuntersky.peregrine.exception.ProductionStorage;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected final Action1<Throwable> errorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            final String title = "ERROR";
            Log.e(TAG, title, throwable);
            showErrorDialog(title, throwable);
        }
    };
    protected EtradeApi etradeApi;
    protected Storage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etradeApi = new EtradeApi(getActivity());
        storage = new ProductionStorage(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    protected void showErrorDialog(String title, Throwable throwable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(throwable.toString())
               .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .show();
    }

    protected Observable<EtradeAccountList> fetchAccountList() {
        final Func1<OauthToken, Observable<List<EtradeAccount>>> accessTokenToAccountList = new Func1<OauthToken,
              Observable<List<EtradeAccount>>>() {
            @Override
            public Observable<List<EtradeAccount>> call(OauthToken oauthToken) {
                return etradeApi.fetchAccountList(oauthToken);
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
                                        storage.eraseOauthAccessToken();
                                    }
                                });
                      } else {
                          return Observable.error(throwable);
                      }
                  }
              })
              .map(new Func1<List<EtradeAccount>, EtradeAccountList>() {
                  @Override
                  public EtradeAccountList call(List<EtradeAccount> etradeAccounts) {
                      return new EtradeAccountList(etradeAccounts, new Date());
                  }
              })
              .doOnNext(new Action1<EtradeAccountList>() {
                  @Override
                  public void call(EtradeAccountList accountList) {
                      storage.writeAccountList(accountList);
                  }
              });
    }

    private Observable<OauthToken> getOauthAccessToken() {
        return storage.readOauthAccessToken()
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
                      return etradeApi.renewOauthAccessToken(oauthToken);
                  }
              });
    }

    private Observable<OauthToken> fetchOauthAccessToken() {
        return etradeApi.fetchOauthRequestToken()
                        .flatMap(new Func1<OauthToken, Observable<OauthVerifier>>() {
                            @Override
                            public Observable<OauthVerifier> call(OauthToken requestToken) {
                                return promptForVerifier(requestToken);
                            }
                        })
                        .flatMap(new Func1<OauthVerifier, Observable<OauthToken>>() {
                            @Override
                            public Observable<OauthToken> call(OauthVerifier verifier) {
                                return etradeApi.fetchOauthAccessToken(verifier);
                            }
                        })
                        .doOnNext(new Action1<OauthToken>() {
                            @Override
                            public void call(OauthToken oauthToken) {
                                storage.writeOauthAccessToken(oauthToken);
                            }
                        });
    }

    private Observable<OauthVerifier> promptForVerifier(final OauthToken oauthRequestToken) {
        return Observable.create(new Observable.OnSubscribe<OauthVerifier>() {
            @Override
            public void call(final Subscriber<? super OauthVerifier> subscriber) {
                final FragmentManager fragmentManager = getChildFragmentManager();
                final VerifierFragment verifierFragment = VerifierFragment.newInstance(etradeApi.oauthAppToken.appKey,
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
                               .add(R.id.frame_verifier, verifierFragment, "VerifierFragment")
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
}
