package com.rubyhuntersky.peregrine;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class OauthRequestToken {

    public static final String TAG = OauthRequestToken.class.getSimpleName();
    private final String requestTokenId;
    private final String requestTokenSecret;

    public OauthRequestToken(String formString) {
        Map<String, String> formValues = getFormValues(formString);
        this.requestTokenId = formValues.get("oauth_token");
        this.requestTokenSecret = formValues.get("oauth_token_secret");
    }

    @Override
    public String toString() {
        return "OauthRequestToken{" +
              "requestTokenId='" + requestTokenId + '\'' +
              ", requestTokenSecret='" + requestTokenSecret + '\'' +
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
