package com.rubyhuntersky.peregrine;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 11/1/15.
 */

public class OauthToken {
    public final String key;
    public final String secret;

    public OauthToken(String formString) {
        Map<String, String> formValues = getFormValues(formString);
        this.key = formValues.get("oauth_token");
        this.secret = formValues.get("oauth_token_secret");
    }

    @Override
    public String toString() {
        return "OauthToken{" +
              "key='" + key + '\'' +
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
