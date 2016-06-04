package com.rubyhuntersky.peregrine.lib.oauth.model;

/**
 * @author wehjin
 * @since 11/1/15.
 */

public class OauthAppToken {

    public final String appKey;
    public final String appSecret;

    public OauthAppToken(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    @Override
    public String toString() {
        return "OauthAppToken{" +
              "appKey='" + appKey + '\'' +
              '}';
    }
}
