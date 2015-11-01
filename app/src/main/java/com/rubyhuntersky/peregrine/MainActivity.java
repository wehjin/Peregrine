package com.rubyhuntersky.peregrine;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView netWorthTextView;
    private TextView refreshTimeTextVIew;
    private EtradeApi etradeApi;
    private Subscription refreshSubscription = Subscriptions.empty();
    private Action1<Throwable> errorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(TAG, "refreshData", throwable);
            showErrorDialog(throwable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        etradeApi = new EtradeApi(this);
    }

    private void initViews() {
        netWorthTextView = (TextView) findViewById(R.id.textview_net_worth);
        refreshTimeTextVIew = (TextView) findViewById(R.id.textview_refresh_time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshData();
                return true;
            case R.id.action_request_token:
                fetchRequestToken();
                return true;
            case R.id.action_verifier:
                fetchVerifier();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void fetchVerifier() {
        etradeApi.getOauthRequestToken()
                 .flatMap(new Func1<OauthRequestToken, Observable<String>>() {
                     @Override
                     public Observable<String> call(OauthRequestToken oauthRequestToken) {
                         return getVerifier(oauthRequestToken);
                     }
                 })
                 .subscribe(new Action1<String>() {
                     @Override
                     public void call(String s) {
                         Log.d(TAG, "Verifier: " + s);
                     }
                 }, errorAction);
    }

    private Observable<String> getVerifier(final OauthRequestToken oauthRequestToken) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final FragmentManager fragmentManager = getFragmentManager();
                final VerifierFragment verifierFragment = VerifierFragment.newInstance(etradeApi.oauthAppToken.appKey,
                                                                                       oauthRequestToken.requestKey);
                verifierFragment.setListener(new VerifierFragment.Listener() {

                    @Override
                    public void onVerifier(String verifier) {
                        fragmentManager.popBackStack();
                        subscriber.onNext(verifier);
                        subscriber.onCompleted();
                    }
                });
                fragmentManager.beginTransaction()
                               .add(R.id.frame_verifier, verifierFragment, "VerifierFragment")
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

    private void fetchRequestToken() {
        etradeApi.getOauthRequestToken()
                 .subscribe(new Action1<OauthRequestToken>() {
                     @Override
                     public void call(OauthRequestToken oauthRequestToken) {
                         Log.d(TAG, oauthRequestToken.toString());
                     }
                 }, errorAction);
    }

    @Override
    protected void onPause() {
        refreshSubscription.unsubscribe();
        super.onPause();
    }

    private void refreshData() {
        refreshSubscription = getNetWorth().subscribe(new Action1<BigDecimal>() {
            @Override
            public void call(BigDecimal bigDecimal) {
                netWorthTextView.setText(String.format("$ %s", "Fake data"));

                final String refreshTime = DateFormat.getDateTimeInstance().format(new Date());
                refreshTimeTextVIew.setText(refreshTime);
            }
        }, errorAction);
    }

    private void showErrorDialog(Throwable throwable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error").setMessage(throwable.toString())
               .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .show();
    }

    private Observable<BigDecimal> getNetWorth() {
        return etradeApi.getAccountList().map(new Func1<List<EtradeAccount>, BigDecimal>() {
            @Override
            public BigDecimal call(List<EtradeAccount> etradeAccounts) {
                BigDecimal sum = BigDecimal.ZERO;
                for (EtradeAccount account : etradeAccounts) {
                    sum = sum.add(account.getNetAccountValue());
                }
                return sum;
            }
        });
    }
}
