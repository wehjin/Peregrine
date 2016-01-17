package com.rubyhuntersky.peregrine.ui.oauth;

import android.app.Activity;

import com.rubyhuntersky.peregrine.OauthToken;
import com.rubyhuntersky.peregrine.OauthVerifier;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * @author wehjin
 * @since 1/17/16.
 */

public class OauthUi {

    public static Observable<OauthVerifier> promptForVerifier(final Activity activity, final OauthToken requestToken) {

        return Observable.create(new Observable.OnSubscribe<OauthVerifier>() {
            @Override
            public void call(final Subscriber<? super OauthVerifier> subscriber) {

                final EtradeVerifierFragment fragment = EtradeVerifierFragment.newInstance(requestToken,
                      new EtradeVerifierFragment.Listener() {
                          @Override
                          public void onVerifier(String verifier) {
                              subscriber.onNext(new OauthVerifier(verifier, requestToken));
                              subscriber.onCompleted();
                          }
                      });
                fragment.show(activity.getFragmentManager(), "VerifierFragment");
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        fragment.dismiss();
                    }
                }));
            }

        });
    }
}
