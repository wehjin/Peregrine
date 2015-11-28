package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Collections;
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
    public static final String ET_OAUTH_URL = "https://etws.etrade.com/oauth/";
    public final OauthAppToken oauthAppToken;
    private final String etRestUrlTemplate;

    public EtradeApi(Context context) {
        oauthAppToken = new OauthAppToken(context.getString(R.string.et_api_key),
                                          context.getString(R.string.et_api_secret));
        etRestUrlTemplate = context.getString(R.string.et_rest_url);
    }

    public Observable<OauthToken> fetchOauthRequestToken() {
        final String urlString = ET_OAUTH_URL + "request_token";
        final OauthHttpRequest request = new OauthHttpRequest.Builder(urlString, oauthAppToken).build();
        return getOauthHttpResponseString(request)
              .map(new Func1<String, OauthToken>() {
                  @Override
                  public OauthToken call(String string) {
                      return new OauthToken(string);
                  }
              });
    }

    public Observable<OauthToken> fetchOauthAccessToken(OauthVerifier verifier) {
        final String urlString = ET_OAUTH_URL + "access_token";
        final OauthHttpRequest request = new OauthHttpRequest.Builder(urlString, oauthAppToken)
              .withVerifier(verifier).build();
        return getOauthHttpResponseString(request).map(new Func1<String, OauthToken>() {
            @Override
            public OauthToken call(String s) {
                return new OauthToken(s);
            }
        });
    }

    public Observable<OauthToken> renewOauthAccessToken(final OauthToken oauthToken) {
        final String urlString = ET_OAUTH_URL + "renew_access_token";
        final OauthHttpRequest request = new OauthHttpRequest.Builder(urlString, oauthAppToken)
              .withToken(oauthToken).build();
        return getOauthHttpResponseString(request).map(new Func1<String, OauthToken>() {
            @Override
            public OauthToken call(String s) {
                Log.d(TAG, "Renewal response: " + s);
                return oauthToken;
            }
        });
    }

    public Observable<JSONObject> fetchAccountPositionsResponse(final String accountId, OauthToken accessToken) {
        Log.d(TAG, "fetchAccountPositionsResponse: " + accountId);
        final String urlString = getEtradeRestUrl("accounts", "accountpositions/" + accountId + ".json");
        final OauthHttpRequest request = new OauthHttpRequest.Builder(urlString, oauthAppToken)
              .withToken(accessToken)
              .build();
        return getOauthHttpResponseString(request).map(new Func1<String, JSONObject>() {
            @Override
            public JSONObject call(String inputResponse) {
                Log.d(TAG, accountId + " " + inputResponse);
                try {
                    return new JSONObject(inputResponse).getJSONObject("json.accountPositionsResponse");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Observable<List<EtradeAccount>> fetchAccountList(OauthToken accessToken) {
        final String url = getEtradeRestUrl("accounts", "accountlist");
        final OauthHttpRequest request = new OauthHttpRequest.Builder(url, oauthAppToken)
              .withToken(accessToken)
              .build();
        return getOauthHttpResponseString(request).map(new Func1<String, List<EtradeAccount>>() {
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

    private Observable<String> getOauthHttpResponseString(OauthHttpRequest request) {
        final String url = request.getOauthUrlWithoutParameters();
        return getHttpResponseString(url, new Pair<>("Authorization", request.getAuthorizationHeader()));
    }

    private Observable<String> getHttpResponseString(final String urlString, final List<Pair<String, String>> headers) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    final URL url = new URL(urlString);
                    HttpURLConnection connection = new OkUrlFactory(new OkHttpClient()).open(url);
                    for (Pair<String, String> headerField : headers) {
                        Log.d(TAG, "Header: " + headerField.first + " " + headerField.second);
                        connection.setRequestProperty(headerField.first, headerField.second);
                    }
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
                                final String message = String.format("%s %d %s", urlString, responseCode,
                                                                     responseMessage);
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

    private Observable<String> getHttpResponseString(String url, Pair<String, String> headerField) {
        return getHttpResponseString(url, Collections.singletonList(headerField));
    }

    private String getEtradeRestUrl(String module, String api) {
        return String.format(etRestUrlTemplate, module, api);
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
