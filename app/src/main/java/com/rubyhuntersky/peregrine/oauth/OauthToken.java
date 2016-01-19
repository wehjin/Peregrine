package com.rubyhuntersky.peregrine.oauth;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 11/1/15.
 */

public class OauthToken {
    public final OauthAppToken appToken;
    public final String key;
    public final String secret;

    public OauthToken(String formString, OauthAppToken appToken) {
        this(getFormValues(formString), appToken);
    }

    private OauthToken(Map<String, String> formValues, OauthAppToken appToken) {
        this(formValues.get("oauth_token"), formValues.get("oauth_token_secret"), appToken);
    }

    public OauthToken(String access_key, String access_secret, OauthAppToken appToken) {
        this.appToken = appToken;
        this.key = access_key;
        this.secret = access_secret;
    }

    @Override
    public String toString() {
        return "OauthToken{" +
              "appToken=" + appToken +
              ", key='" + key + '\'' +
              ", secret='" + secret + '\'' +
              '}';
    }

    @NonNull
    private static Map<String, String> getFormValues(String formString) {
        Map<String, String> formValues = new HashMap<>();
        final String[] equalities = formString.split("[&]");
        for (String equality : equalities) {
            final String[] keyAndValue = equality.split("[=]");
            formValues.put(keyAndValue[0], Uri.decode(keyAndValue[1]));
        }
        return formValues;
    }
}
