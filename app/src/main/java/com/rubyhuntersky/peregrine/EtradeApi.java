package com.rubyhuntersky.peregrine;

import android.support.annotation.NonNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class EtradeApi {

    public Observable<List<EtradeAccount>> getAccountList() {
        final Observable<List<EtradeAccount>> resumeSequence = getAccessToken().flatMap(
              new Func1<OauthAccessToken, Observable<List<EtradeAccount>>>() {
                  @Override
                  public Observable<List<EtradeAccount>> call(OauthAccessToken oauthAccessToken) {
                      return getJustAccountList();
                  }
              });
        return getJustAccountList().onErrorResumeNext(resumeSequence);
    }

    private Observable<List<EtradeAccount>> getJustAccountList() {
        final String spec = "https://etws.etrade.com/accounts/rest/accountlist";
        return getHttpInputStream(spec).map(new Func1<InputStream, List<EtradeAccount>>() {
            @Override
            public List<EtradeAccount> call(InputStream inputStream) {
                return null;
            }
        });
    }

    private Observable<OauthAccessToken> getAccessToken() {
        return getJustOauthAccessToken().onErrorResumeNext(
              getOauthRequestToken().flatMap(new Func1<OauthRequestToken, Observable<OauthAccessToken>>() {
                  @Override
                  public Observable<OauthAccessToken> call(OauthRequestToken oauthRequestToken) {
                      return getJustOauthAccessToken();
                  }
              }));
    }

    @NonNull
    private Observable<OauthAccessToken> getJustOauthAccessToken() {
        return getHttpInputStream("https://etws.etrade.com/oauth/access_token").map(
              new Func1<InputStream, OauthAccessToken>() {
                  @Override
                  public OauthAccessToken call(InputStream inputStream) {
                      return null;
                  }
              });
    }

    private Observable<OauthRequestToken> getOauthRequestToken() {
        return getHttpInputStream("https://etws.etrade.com/oauth/request_token").map(
              new Func1<InputStream, OauthRequestToken>() {
                  @Override
                  public OauthRequestToken call(InputStream inputStream) {
                      return null;
                  }
              });
    }

    private Observable<InputStream> getHttpInputStream(final String urlString) {
        return Observable.create(new Observable.OnSubscribe<InputStream>() {
            @Override
            public void call(Subscriber<? super InputStream> subscriber) {
                try {
                    final URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    final int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        final String responseMessage = connection.getResponseMessage();
                        Throwable error;
                        switch (responseCode) {
                            case 401:
                                error = new NotAuthorizedException(urlString + "\n" + responseMessage);
                                break;
                            default:
                                final String message = String.format("%d %s", responseCode, responseMessage);
                                error = new RuntimeException(message);
                                break;
                        }
                        subscriber.onError(error);
                        return;
                    }

                    final InputStream inputStream = connection.getInputStream();
                    subscriber.onError(new RuntimeException("getAccountList not implemented"));
                } catch (java.io.IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    static public class NotAuthorizedException extends RuntimeException {
        public NotAuthorizedException() {
        }

        public NotAuthorizedException(String detailMessage) {
            super(detailMessage);
        }

        public NotAuthorizedException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public NotAuthorizedException(Throwable throwable) {
            super(throwable);
        }
    }
}
