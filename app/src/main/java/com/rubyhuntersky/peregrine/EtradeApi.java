package com.rubyhuntersky.peregrine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
    private final String appKey;
    private final String appSecret;
    private final String signatureMethod;
    public static final Random RANDOM = new Random();

    public EtradeApi(Context context) {
        appKey = context.getString(R.string.et_production_key);
        appSecret = context.getString(R.string.et_production_secret);
        signatureMethod = "HMAC-SHA1";
    }

    public Observable<OauthRequestToken> getOauthRequestToken() {

        final String urlString = "https://etws.etrade.com/oauth/request_token";

        //TODO percent encode
        List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("oauth_callback", "oob"));
        pairs.add(new Pair<>("oauth_consumer_key", appKey));
        pairs.add(new Pair<>("oauth_nonce", getNonce(16)));
        pairs.add(new Pair<>("oauth_signature_method", signatureMethod));
        pairs.add(new Pair<>("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000)));
        pairs.add(new Pair<>("oauth_version", "1.0"));

        final StringBuilder stringBuilder = new StringBuilder();
        boolean firstPair = true;
        for (Pair<String, String> pair : pairs) {
            if (firstPair) {
                firstPair = false;
            } else {
                stringBuilder.append('&');
            }
            stringBuilder.append(pair.first);
            stringBuilder.append('=');
            stringBuilder.append(oauthPercentEncode(pair.second));
        }
        final String parametersBase = stringBuilder.toString();
        Log.d(TAG, "Parameters base: " + parametersBase);
        final String signatureBase = "POST&" + oauthPercentEncode(urlString) + "&" + oauthPercentEncode(parametersBase);
        Log.d(TAG, "Signature base: " + signatureBase);
        final String signingKey = oauthPercentEncode(appSecret) + '&';
        final String signature;
        try {
            signature = hmacSha1(signatureBase, signingKey);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        Log.d(TAG, "Nonce: " + getNonce(16) + ", signature: " + signature);
        String fullUrl = urlString + "?" + parametersBase + "&oauth_signature=" + oauthPercentEncode(signature);
        return getHttpInputStream(fullUrl).map(new Func1<InputStream, OauthRequestToken>() {
            @Override
            public OauthRequestToken call(InputStream inputStream) {
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

    private String getNonce(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytesToLowercaseHex(bytes);
    }

    private static String hmacSha1(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
        return Base64.encodeToString(mac.doFinal(value.getBytes()), Base64.DEFAULT).trim();
    }

    private final static char[] hexUppercaseArray = "0123456789ABCDEF".toCharArray();

    private String oauthPercentEncode(String s) {
        final StringBuilder stringBuilder = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if ((c >= 0x30 && c <= 0x39) || (c >= 0x41 && c <= 0x5a) || (c >= 0x61 && c <= 0x7a) || c == 0x2d || c ==
                  0x2e || c == 0x5f || c == 0x7e) {
                stringBuilder.append(c);
            } else {
                stringBuilder.append('%');
                stringBuilder.append(hexUppercaseArray[(c >> 4) & 0x0f]);
                stringBuilder.append(hexUppercaseArray[c & 0x0f]);
            }
        }
        return stringBuilder.toString();
    }

    private final static char[] hexLowercaseArray = "0123456789abcdef".toCharArray();

    private static String bytesToLowercaseHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            hexChars[i * 2] = hexLowercaseArray[b >>> 4];
            hexChars[i * 2 + 1] = hexLowercaseArray[b & 0x0F];
        }
        return new String(hexChars);
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
