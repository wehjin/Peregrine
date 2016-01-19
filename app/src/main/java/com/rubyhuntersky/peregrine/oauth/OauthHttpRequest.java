package com.rubyhuntersky.peregrine.oauth;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Pair;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author wehjin
 * @since 11/1/15.
 */

public class OauthHttpRequest {

    public static final String TAG = OauthHttpRequest.class.getSimpleName();
    private final static char[] HEX_UPPERCASE_ARRAY = "0123456789ABCDEF".toCharArray();
    private final static char[] HEX_LOWERCASE_ARRAY = "0123456789abcdef".toCharArray();
    public static final Random RANDOM = new Random();
    private String appSecret;
    private String urlString;
    private String method = "GET";
    private final List<Pair<String, String>> parameters = new ArrayList<>();
    private String requestSecret;

    public String getAuthorizationHeader() {
        return "OAuth realm=\"\"," + getQueryParametersWithSignature().replace('&', ',');
    }

    public String getOauthUrlWithParameters() {
        return urlString + "?" + getQueryParametersWithSignature();
    }

    public String getOauthUrlWithoutParameters() {
        return urlString;
    }

    @NonNull
    private String getQueryParametersWithSignature() {
        final StringBuilder parametersBaseBuilder = new StringBuilder();
        boolean firstPair = true;
        for (Pair<String, String> pair : parameters) {
            if (firstPair) {
                firstPair = false;
            } else {
                parametersBaseBuilder.append("&");
            }
            parametersBaseBuilder.append(pair.first);
            parametersBaseBuilder.append('=');
            parametersBaseBuilder.append(oauthPercentEncode(pair.second));
        }
        final String parametersBase = parametersBaseBuilder.toString();
        final String signatureBase = method + '&' + oauthPercentEncode(urlString) + '&' + oauthPercentEncode(
              parametersBase);
        final String signingKey = oauthPercentEncode(
              appSecret) + '&' + (requestSecret == null ? "" : oauthPercentEncode(requestSecret));
        final String signature;
        try {
            signature = hmacSha1(signatureBase, signingKey);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return parametersBase + "&oauth_signature=" + oauthPercentEncode(signature);
    }

    public String getMethod() {
        return method;
    }

    private static String hmacSha1(String value, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
        return Base64.encodeToString(mac.doFinal(value.getBytes()), Base64.DEFAULT).trim();
    }

    private String oauthPercentEncode(String s) {
        final StringBuilder stringBuilder = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if ((c >= 0x30 && c <= 0x39) || (c >= 0x41 && c <= 0x5a) || (c >= 0x61 && c <= 0x7a) || c == 0x2d || c ==
                  0x2e || c == 0x5f || c == 0x7e) {
                stringBuilder.append(c);
            } else {
                stringBuilder.append('%');
                stringBuilder.append(HEX_UPPERCASE_ARRAY[(c >> 4) & 0x0f]);
                stringBuilder.append(HEX_UPPERCASE_ARRAY[c & 0x0f]);
            }
        }
        return stringBuilder.toString();
    }


    private static String getNonce(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytesToLowercaseHex(bytes);
    }

    private static String bytesToLowercaseHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_LOWERCASE_ARRAY[b >>> 4];
            hexChars[i * 2 + 1] = HEX_LOWERCASE_ARRAY[b & 0x0F];
        }
        return new String(hexChars);
    }


    public static class Builder {
        OauthHttpRequest request = new OauthHttpRequest();

        public Builder(String urlString, OauthAppToken appToken) {
            request.urlString = urlString;
            request.appSecret = appToken.appSecret;
            request.parameters.add(new Pair<>("oauth_callback", "oob"));
            request.parameters.add(new Pair<>("oauth_consumer_key", appToken.appKey));
            request.parameters.add(new Pair<>("oauth_nonce", getNonce(16)));
            request.parameters.add(new Pair<>("oauth_signature_method", "HMAC-SHA1"));
            request.parameters.add(new Pair<>("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000)));
            request.parameters.add(new Pair<>("oauth_version", "1.0"));
        }

        public OauthHttpRequest build() {
            return request;
        }


        public Builder withVerifier(OauthVerifier verifier) {
            withToken(verifier.requestToken);
            request.parameters.add(6, new Pair<>("oauth_verifier", verifier.verifier));
            return this;
        }

        public Builder withToken(OauthToken token) {
            request.requestSecret = token.secret;
            request.parameters.add(5, new Pair<>("oauth_token", token.key));
            return this;
        }
    }
}
