package com.rubyhuntersky.peregrine;


import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.exception.NotStoredException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class NetValueFragment extends BaseFragment {

    public static final String TAG = NetValueFragment.class.getSimpleName();

    private TextView netWorthTextView;
    private TextView refreshTimeTextVIew;
    private Action1<EtradeAccountList> updateSubviewsFromAccountList = new Action1<EtradeAccountList>() {
        @Override
        public void call(EtradeAccountList accountList) {
            netWorthTextView.setText(NumberFormat.getCurrencyInstance().format(getNetWorth(accountList.accounts)));
            refreshTimeTextVIew.setText(getRelativeTimeString(accountList.arrivalDate.getTime()));
        }
    };

    public NetValueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_net_value, container, false);
        initSubviewFields(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getStorage().readAccountList().subscribe(updateSubviewsFromAccountList, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (throwable instanceof NotStoredException) {
                    return;
                }
                errorAction.call(throwable);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchAccountList().subscribe(updateSubviewsFromAccountList, errorAction);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.net_value, menu);
    }


    private static BigDecimal getNetWorth(List<EtradeAccount> etradeAccounts) {
        BigDecimal sum = BigDecimal.ZERO;
        for (EtradeAccount account : etradeAccounts) {
            sum = sum.add(account.getNetAccountValue());
        }
        return sum;
    }

    private CharSequence getRelativeTimeString(long time) {
        final long elapsed = new Date().getTime() - time;
        if (elapsed >= 0 && elapsed < 60000) {
            return "seconds ago";
        } else {
            return DateUtils.getRelativeTimeSpanString(time);
        }
    }

    private void initSubviewFields(View view) {
        netWorthTextView = (TextView) view.findViewById(R.id.textview_net_worth);
        refreshTimeTextVIew = (TextView) view.findViewById(R.id.textview_refresh_time);
    }
}
