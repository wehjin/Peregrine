package com.rubyhuntersky.peregrine;

/**
 * @author wehjin
 * @since 11/1/15.
 */

public class OauthVerifier {
    public final String verifier;
    public final OauthRequestToken requestToken;

    public OauthVerifier(String verifier, OauthRequestToken requestToken) {
        this.verifier = verifier;
        this.requestToken = requestToken;
    }

    @Override
    public String toString() {
        return "OauthVerifier{" +
              "verifier='" + verifier + '\'' +
              ", requestToken=" + requestToken +
              '}';
    }
}
