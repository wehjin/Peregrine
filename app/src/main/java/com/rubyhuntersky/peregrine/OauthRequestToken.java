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
    public final String requestKey;
    public final String requestSecret;

    public OauthRequestToken(String formString) {
        Map<String, String> formValues = getFormValues(formString);
        this.requestKey = formValues.get("oauth_token");
        this.requestSecret = formValues.get("oauth_token_secret");
    }

    @Override
    public String toString() {
        return "OauthRequestToken{" +
              "requestKey='" + requestKey + '\'' +
              ", requestSecret='" + requestSecret + '\'' +
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
