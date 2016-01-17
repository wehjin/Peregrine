package com.rubyhuntersky.peregrine.ui.oauth;

import android.app.Activity;
import android.app.FragmentManager;

import com.rubyhuntersky.peregrine.OauthToken;
import com.rubyhuntersky.peregrine.OauthVerifier;
import com.rubyhuntersky.peregrine.R;

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
                final FragmentManager fragmentManager = activity.getFragmentManager();
                final VerifierFragment verifierFragment = VerifierFragment.newInstance(requestToken.appToken.appKey,
                                                                                       requestToken.key);
                verifierFragment.setListener(new VerifierFragment.Listener() {

                    @Override
                    public void onVerifier(String verifier) {
                        fragmentManager.popBackStack();
                        subscriber.onNext(new OauthVerifier(verifier, requestToken));
                        subscriber.onCompleted();
                    }
                });
                fragmentManager.beginTransaction()
                               .add(R.id.verifier_frame, verifierFragment, "VerifierFragment")
                               .addToBackStack(null)
                               .commit();
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        if (fragmentManager.findFragmentByTag("VerifierFragment") == null) {
                            return;
                        }
                        fragmentManager.popBackStack();
                    }
                }));
            }
        });
    }
}
