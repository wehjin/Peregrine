package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rubyhuntersky.columnui.Coloret;
import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.Heightlet;
import com.rubyhuntersky.columnui.Ui;
import com.rubyhuntersky.columnui.UiView;
import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 1/19/16.
 */
public class BuyDialogFragment extends AppCompatDialogFragment {

    public static final String AMOUNT_KEY = "amountKey";
    private BigDecimal buyAmount = BigDecimal.ZERO;
    private Column column;
    private UiView uiView;
    private Ui panel;

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
        panel = Creator.createPanel(Coloret.BLUE, Heightlet.HUNDRED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        uiView.setUi(panel);
    }

    @Override
    public void onPause() {
        uiView.clearUi();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_buy, container, false);
        uiView = (UiView) inflate.findViewById(R.id.ui);
        return inflate;
    }
}
