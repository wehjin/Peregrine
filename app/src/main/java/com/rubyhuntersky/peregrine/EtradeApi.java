package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

    public Observable<OauthToken> fetchOauthRequestToken() {

        final String oauthUrl = new OauthHttpRequest.Builder("https://etws.etrade.com/oauth/request_token",
                                                             oauthAppToken).build().getOauthUrl();
        return getHttpResponseString(oauthUrl).map(new Func1<String, OauthToken>() {
            @Override
            public OauthToken call(String string) {
                return new OauthToken(string);
            }
        });
    }

    public Observable<OauthToken> fetchOauthAccessToken(OauthVerifier verifier) {
        final String oauthUrl = new OauthHttpRequest.Builder("https://etws.etrade.com/oauth/access_token",
                                                             oauthAppToken)
              .withVerifier(verifier).build().getOauthUrl();
        return getHttpResponseString(oauthUrl).map(new Func1<String, OauthToken>() {
            @Override
            public OauthToken call(String s) {
                return new OauthToken(s);
            }
        });
    }

    public Observable<OauthToken> renewOauthAccessToken(final OauthToken oauthToken) {
        final String oauthUrl = new OauthHttpRequest.Builder("https://etws.etrade.com/oauth/renew_access_token",
                                                             oauthAppToken)
              .withToken(oauthToken).build().getOauthUrl();
        return getHttpResponseString(oauthUrl).map(new Func1<String, OauthToken>() {
            @Override
            public OauthToken call(String s) {
                Log.d(TAG, "Renewal response: " + s);
                return oauthToken;
            }
        });
    }

    public Observable<List<EtradeAccount>> fetchAccountList(OauthToken accessToken) {
        final String oauthUrl = new OauthHttpRequest.Builder("https://etws.etrade.com/accounts/rest/accountlist",
                                                             oauthAppToken)
              .withToken(accessToken).build().getOauthUrl();
        return getHttpResponseString(oauthUrl).map(new Func1<String, List<EtradeAccount>>() {
            @Override
            public List<EtradeAccount> call(String inputResponse) {
                List<EtradeAccount> etradeAccounts = new ArrayList<>();

                Document document;
                try {
                    document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(
                          new ByteArrayInputStream(inputResponse.getBytes()));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw (new RuntimeException(e));
                }

                final NodeList accountNodes = document.getElementsByTagName("Account");
                final int count = accountNodes.getLength();
                for (int i = 0; i < count; i++) {
                    etradeAccounts.add(new EtradeAccount((Element) accountNodes.item(i)));
                }
                return etradeAccounts;
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

    @SuppressWarnings("unused")
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
