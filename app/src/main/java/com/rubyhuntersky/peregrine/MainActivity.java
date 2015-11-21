package com.rubyhuntersky.peregrine;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.exception.NotStoredException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView netWorthTextView;
    private TextView refreshTimeTextVIew;
    private Action1<EtradeAccountList> updateSubviewsFromAccountList = new Action1<EtradeAccountList>() {
        @Override
        public void call(EtradeAccountList accountList) {
            netWorthTextView.setText(NumberFormat.getCurrencyInstance().format(getNetWorth(accountList.accounts)));
            refreshTimeTextVIew.setText(getRelativeTimeString(accountList.arrivalDate.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSubviewFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        storage.readAccountList().subscribe(updateSubviewsFromAccountList, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (throwable instanceof NotStoredException) {
                    return;
                }
                errorAction.call(throwable);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchAccountList().subscribe(updateSubviewsFromAccountList, errorAction);
                return true;
            case R.id.action_go:
                go();
                return true;
            case R.id.action_view_assets:
                startActivity(new Intent(this, AssetsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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

    private void go() {
        renewOauthAccessToken()
              .subscribe(new Action1<OauthToken>() {
                  @Override
                  public void call(OauthToken oauthToken) {
                      Log.d(TAG, "Renewed token: " + oauthToken);
                  }
              }, errorAction);
    }

    private Observable<EtradeAccountList> fetchAccountList() {
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
                final FragmentManager fragmentManager = getFragmentManager();
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

    private static BigDecimal getNetWorth(List<EtradeAccount> etradeAccounts) {
        BigDecimal sum = BigDecimal.ZERO;
        for (EtradeAccount account : etradeAccounts) {
            sum = sum.add(account.getNetAccountValue());
        }
        return sum;
    }

    private void initSubviewFields() {
        netWorthTextView = (TextView) findViewById(R.id.textview_net_worth);
        refreshTimeTextVIew = (TextView) findViewById(R.id.textview_refresh_time);
    }

    private CharSequence getRelativeTimeString(long time) {
        final long elapsed = new Date().getTime() - time;
        if (elapsed >= 0 && elapsed < 60000) {
            return "seconds ago";
        } else {
            return DateUtils.getRelativeTimeSpanString(time);
        }
    }
}
