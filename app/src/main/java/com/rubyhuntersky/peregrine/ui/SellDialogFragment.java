package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.AssetPrice;
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

    public static final String AMOUNT_KEY = "amountKey";
    public static final String PRICES_KEY = "pricesKey";
    public static final String SELECTED_PRICE_KEY = "selectedPriceKey";
    private BigDecimal amount = BigDecimal.ZERO;
    private BehaviorSubject<List<AssetPrice>> prices = BehaviorSubject.create(Collections.<AssetPrice>emptyList());
    private BehaviorSubject<AssetPrice> selectedPrice = BehaviorSubject.create(new AssetPrice("-", BigDecimal.ZERO));

    public static SellDialogFragment create(BigDecimal amount, List<AssetPrice> priceOptions,
          AssetPrice selectedPrice) {

        final Bundle arguments = new Bundle();
        arguments.putSerializable(AMOUNT_KEY, amount);
        arguments.putParcelableArrayList(PRICES_KEY, new ArrayList<>(priceOptions));
        arguments.putParcelable(SELECTED_PRICE_KEY, selectedPrice);

        final SellDialogFragment sellDialogFragment = new SellDialogFragment();
        sellDialogFragment.setArguments(arguments);
        return sellDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

        amount = (BigDecimal) getArguments().getSerializable(AMOUNT_KEY);

        final Bundle bundle = savedInstanceState == null ? getArguments() : savedInstanceState;
        prices.onNext(bundle.<AssetPrice>getParcelableArrayList(PRICES_KEY));
        selectedPrice.onNext(bundle.<AssetPrice>getParcelable(SELECTED_PRICE_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PRICES_KEY, new ArrayList<>(prices.getValue()));
        outState.putParcelable(SELECTED_PRICE_KEY, selectedPrice.getValue());
        super.onSaveInstanceState(outState);
    }

    @NonNull
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

        final TextView amountText = (TextView) inflate.findViewById(R.id.amountText);
        amountText.setText("Sell " + UiHelper.getCurrencyDisplayString(amount));

        final Spinner priceSpinner = (Spinner) inflate.findViewById(R.id.priceSpinner);
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPrice.onNext(prices.getValue().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        prices.map(new Func1<List<AssetPrice>, List<String>>() {
            @Override
            public List<String> call(List<AssetPrice> prices) {
                return Observable.from(prices).map(new Func1<AssetPrice, String>() {
                    @Override
                    public String call(AssetPrice price) {
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
                priceSpinner.setSelection(prices.getValue().indexOf(selectedPrice.getValue()));
            }
        });

        final TextView sharesText = (TextView) inflate.findViewById(R.id.sharesText);
        selectedPrice.subscribe(new Action1<AssetPrice>() {
            @Override
            public void call(AssetPrice price) {
                sharesText.setText(price.getSharesString(amount));
            }
        });
        return inflate;
    }


}
