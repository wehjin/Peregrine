package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rubyhuntersky.columnui.BarUi;
import com.rubyhuntersky.columnui.ColumnUi;
import com.rubyhuntersky.columnui.ColumnUiView;
import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.material.Android;
import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.rubyhuntersky.columnui.Creator.barColumn;
import static com.rubyhuntersky.columnui.Creator.colorBar;
import static com.rubyhuntersky.columnui.Creator.createDarkImportant;
import static com.rubyhuntersky.columnui.Creator.createPanel;
import static com.rubyhuntersky.columnui.Creator.textTile;
import static com.rubyhuntersky.columnui.basics.Coloret.BLACK;
import static com.rubyhuntersky.columnui.basics.Coloret.GREEN;
import static com.rubyhuntersky.columnui.basics.Coloret.WHITE;
import static com.rubyhuntersky.columnui.basics.Sizelet.FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.HALF_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.CONTEXT;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.READABLE;
import static com.rubyhuntersky.columnui.basics.Sizelet.THIRD_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.ofPortion;

/**
 * @author wehjin
 * @since 1/19/16.
 */
public class BuyDialogFragment extends AppCompatDialogFragment {

    public static final String AMOUNT_KEY = "amountKey";
    private BigDecimal buyAmount = BigDecimal.ZERO;
    private ColumnUiView columnUiView;
    private ColumnUi panel;

    public static BuyDialogFragment create(BigDecimal amount) {

        final Bundle arguments = new Bundle();
        arguments.putSerializable(AMOUNT_KEY, amount);
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

        final String buyString = "Buy $3,000.00";
        final String sharesString = "28 shares";
        final List<String> symbols = new ArrayList<>();
        symbols.add("IBM $3.50");
        symbols.add("MSFT $2.00");
        String symbol = symbols.get(0);
        final ColumnUi spinner = Android.createSpinner(symbols, symbol);

        final ColumnUi spacing = createPanel(THIRD_FINGER, null);
        final ColumnUi divider = createPanel(ofPortion(.1f, READABLE), BLACK);

        final BarUi obelusBarUi = Creator.tileBar(textTile("\u00f7", TextStylet.IMPORTANT_DARK));
        final BarUi assetSelectionBar = colorBar(GREEN, FINGER).expandStart(obelusBarUi);
        this.panel = createDarkImportant(buyString).padTop(HALF_FINGER)
                                                   .placeAbove(spinner)
                                                   .placeAbove(barColumn(FINGER, assetSelectionBar))
                                                   .placeAbove(divider)
                                                   .placeAbove(spacing)
                                                   .placeAbove(createDarkImportant(sharesString))
                                                   .padBottom(THIRD_FINGER)
                                                   .padHorizontal(THIRD_FINGER)
                                                   .placeBefore(createPanel(new Sizelet(0, 1, CONTEXT), WHITE), 0);
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
