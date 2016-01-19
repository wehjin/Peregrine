package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * @author wehjin
 * @since 1/19/16.
 */
public class SellDialogFragment extends AppCompatDialogFragment {

    private BigDecimal amount = BigDecimal.ZERO;
    private BehaviorSubject<List<Price>> prices = BehaviorSubject.create(Collections.<Price>emptyList());

    private TextView amountText;
    private Spinner priceSpinner;
    private TextView sharesText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        amount = BigDecimal.valueOf(25000);

        final List<Price> priceList = new ArrayList<>();
        priceList.add(new Price("TSLA", BigDecimal.valueOf(250.36)));
        priceList.add(new Price("SCTY", BigDecimal.valueOf(35.36)));
        prices.onNext(priceList);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_sell, container, false);
        amountText = (TextView) inflate.findViewById(R.id.amountText);
        amountText.setText("Sell " + UiHelper.getCurrencyDisplayString(amount));

        priceSpinner = (Spinner) inflate.findViewById(R.id.priceSpinner);
        prices.map(new Func1<List<Price>, List<String>>() {
            @Override
            public List<String> call(List<Price> prices) {
                return Observable.from(prices).map(new Func1<Price, String>() {
                    @Override
                    public String call(Price price) {
                        return price.name + " " + UiHelper.getCurrencyDisplayString(price.amount);
                    }
                }).toList().toBlocking().first();
            }
        }).map(new Func1<List<String>, SpinnerAdapter>() {
            @Override
            public SpinnerAdapter call(List<String> strings) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                      android.R.layout.simple_spinner_item, strings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                return adapter;
            }
        }).subscribe(new Action1<SpinnerAdapter>() {
            @Override
            public void call(SpinnerAdapter spinnerAdapter) {
                priceSpinner.setAdapter(spinnerAdapter);
            }
        });

        sharesText = (TextView) inflate.findViewById(R.id.sharesText);
        sharesText.setText("300 shares");
        return inflate;
    }


    private static class Price {
        public final String name;
        public final BigDecimal amount;

        public Price(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
