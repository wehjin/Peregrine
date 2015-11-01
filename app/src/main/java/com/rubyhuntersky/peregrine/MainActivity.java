package com.rubyhuntersky.peregrine;

import android.app.AlertDialog;
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

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView netWorthTextView;
    private TextView refreshTimeTextVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData() {
        getNetWorth().subscribe(new Action1<BigDecimal>() {
            @Override
            public void call(BigDecimal bigDecimal) {
                netWorthTextView.setText(String.format("$ %s", "Fake data"));

                final String refreshTime = DateFormat.getDateTimeInstance().format(new Date());
                refreshTimeTextVIew.setText(refreshTime);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "refreshData", throwable);
                showErrorDialog(throwable);
            }
        });
    }

    private void showErrorDialog(Throwable throwable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error")
               .setMessage(throwable.getMessage())
               .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .show();
    }

    private Observable<BigDecimal> getNetWorth() {
        return Observable.create(new Observable.OnSubscribe<BigDecimal>() {
            @Override
            public void call(Subscriber<? super BigDecimal> subscriber) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }
}
