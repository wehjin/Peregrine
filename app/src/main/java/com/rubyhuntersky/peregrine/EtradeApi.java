package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

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

    public static final String TAG = EtradeApi.class.getSimpleName();
    public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    public final OauthAppToken oauthAppToken;

    public EtradeApi(Context context) {
        oauthAppToken = new OauthAppToken(context.getString(R.string.et_production_key),
                                          context.getString(R.string.et_production_secret));
    }

    public Observable<OauthRequestToken> getOauthRequestToken() {

        final String url = "https://etws.etrade.com/oauth/request_token";
        final OauthHttpRequest request = new OauthHttpRequest.Builder(url, oauthAppToken).build();
        return getHttpResponseString(request.getOauthUrl()).map(new Func1<String, OauthRequestToken>() {
            @Override
            public OauthRequestToken call(String string) {
                return new OauthRequestToken(string);
            }
        });
    }

    public Observable<OauthAccessToken> getOauthAccessToken(OauthVerifier verifier) {
        final String url = "https://etws.etrade.com/oauth/access_token";
        final OauthHttpRequest request = new OauthHttpRequest.Builder(url, oauthAppToken)
              .withVerifier(verifier)
              .build();
        return getHttpResponseString(request.getOauthUrl()).map(new Func1<String, OauthAccessToken>() {
            @Override
            public OauthAccessToken call(String s) {
                Log.d(TAG, "Access response: " + s);
                return null;
            }
        });
    }

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
        return getHttpResponseString(spec).map(new Func1<String, List<EtradeAccount>>() {
            @Override
            public List<EtradeAccount> call(String inputStream) {
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
        return getHttpResponseString("https://etws.etrade.com/oauth/access_token").map(
              new Func1<String, OauthAccessToken>() {
                  @Override
                  public OauthAccessToken call(String inputStream) {
                      return null;
                  }
              });
    }

    private Observable<String> getHttpResponseString(final String urlString) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
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

                    Log.d(TAG, "Content-Type: " + connection.getContentType());
                    final byte[] responseBytes = getBytes(connection.getInputStream());
                    final String responseString = new String(responseBytes, "UTF-8");
                    subscriber.onNext(responseString);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int numberRead;
        byte[] data = new byte[16384];
        while ((numberRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numberRead);
        }
        buffer.flush();
        return buffer.toByteArray();
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
