package com.rubyhuntersky.peregrine.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.AccountsList;
import com.rubyhuntersky.peregrine.EtradeAccount;
import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.exception.NotStoredException;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class NetValueFragment extends BaseFragment {

    public static final String TAG = NetValueFragment.class.getSimpleName();

    private TextView netWorthTextView;
    private TextView refreshTimeTextVIew;
    private Action1<AccountsList> updateSubviewsFromAccountList = new Action1<AccountsList>() {
        @Override
        public void call(AccountsList accountsList) {
            final String centerString = accountsList == null ? "No data" : getFormattedNetWorth(accountsList);

            final long count = accountsList == null ? 0 : accountsList.accounts.size();
            final CharSequence cornerString = String.format("%d", count);
            netWorthTextView.setText(centerString);
            refreshTimeTextVIew.setText(cornerString);
        }
    };

    @NonNull
    private String getFormattedNetWorth(AccountsList accountList) {
        return UiHelper.getCurrencyDisplayString(getNetWorth(accountList.accounts));
    }

    private Subscription accountsListSubscription;

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
        accountsListSubscription = getAccountsListStream()
              .subscribe(updateSubviewsFromAccountList,
                         new Action1<Throwable>() {
                             @Override
                             public void call(Throwable throwable) {
                                 if (throwable instanceof
                                       NotStoredException) {
                                     return;
                                 }
                                 getErrorAction().call(throwable);
                             }
                         });
    }

    @Override
    public void onPause() {
        accountsListSubscription.unsubscribe();
        super.onPause();
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


    private void initSubviewFields(View view) {
        netWorthTextView = (TextView) view.findViewById(R.id.textview_net_worth);
        refreshTimeTextVIew = (TextView) view.findViewById(R.id.textview_refresh_time);
    }

    private Observable<AccountsList> getAccountsListStream() {
        return getBaseActivity().getAccountsListStream();
    }
}
