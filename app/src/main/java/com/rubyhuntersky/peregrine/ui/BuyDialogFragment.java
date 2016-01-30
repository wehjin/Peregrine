package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rubyhuntersky.columnui.ColumnUi;
import com.rubyhuntersky.columnui.ColumnUiView;
import com.rubyhuntersky.peregrine.AssetPrice;
import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.rubyhuntersky.columnui.Creator.colorColumn;
import static com.rubyhuntersky.columnui.Creator.textColumn;
import static com.rubyhuntersky.columnui.Creator.textTile;
import static com.rubyhuntersky.columnui.basics.Coloret.BLACK;
import static com.rubyhuntersky.columnui.basics.Coloret.WHITE;
import static com.rubyhuntersky.columnui.basics.Sizelet.FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.HALF_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.PREVIOUS;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.READABLE;
import static com.rubyhuntersky.columnui.basics.Sizelet.THIRD_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.ofPortion;
import static com.rubyhuntersky.columnui.basics.TextStylet.IMPORTANT_DARK;
import static com.rubyhuntersky.columnui.material.Android.spinnerBar;

/**
 * @author wehjin
 * @since 1/19/16.
 */
public class BuyDialogFragment extends AppCompatDialogFragment {

    public static final String DIVISION_SIGN = "\u00f7";
    public static final String AMOUNT_KEY = "amountKey";
    public static final String PRICES_KEY = "pricesKey";
    public static final String SELECTED_PRICE_KEY = "selectedPriceKey";
    public static final ColumnUi SPACING = colorColumn(THIRD_FINGER, null);
    public static final ColumnUi DIVIDER = colorColumn(ofPortion(.1f, READABLE), BLACK);

    private BigDecimal buyAmount = BigDecimal.ZERO;
    private List<AssetPrice> prices;
    private AssetPrice selectedPrice;

    private ColumnUiView columnUiView;
    private ColumnUi panel;

    public static BuyDialogFragment create(BigDecimal amount, List<AssetPrice> prices, AssetPrice selectedPrice) {

        final Bundle arguments = new Bundle();
        arguments.putSerializable(AMOUNT_KEY, amount);
        arguments.putParcelableArrayList(PRICES_KEY, new ArrayList<>(prices));
        arguments.putParcelable(SELECTED_PRICE_KEY, selectedPrice);

        final BuyDialogFragment fragment = new BuyDialogFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

        buyAmount = (BigDecimal) getArguments().getSerializable(AMOUNT_KEY);
        prices = getArguments().getParcelableArrayList(PRICES_KEY);
        selectedPrice = getArguments().getParcelable(SELECTED_PRICE_KEY);

        final String buyString = "Buy " + UiHelper.getCurrencyDisplayString(buyAmount);
        final List<String> symbols = new ArrayList<>();
        for (AssetPrice price : prices) {
            symbols.add(price.name + " " + UiHelper.getCurrencyDisplayString(price.amount));
        }
        String symbol = symbols.get(prices.indexOf(selectedPrice));

        final String sharesString = "28 shares";
        final ColumnUi amountColumn = textColumn(buyString, IMPORTANT_DARK);
        final ColumnUi pricesColumn = spinnerBar(symbols, symbol).expandStart(textTile(DIVISION_SIGN, IMPORTANT_DARK))
                                                                 .toColumn(FINGER);
        this.panel = amountColumn.padTop(HALF_FINGER)
                                 .placeAbove(pricesColumn)
                                 .placeAbove(DIVIDER)
                                 .placeAbove(SPACING)
                                 .placeAbove(textColumn(sharesString, IMPORTANT_DARK))
                                 .padBottom(THIRD_FINGER)
                                 .padHorizontal(THIRD_FINGER)
                                 .placeBefore(colorColumn(PREVIOUS, WHITE), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_buy, container, false);
        columnUiView = (ColumnUiView) inflate.findViewById(R.id.ui);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        columnUiView.setUi(panel);
    }

    @Override
    public void onPause() {
        columnUiView.clearUi();
        super.onPause();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
