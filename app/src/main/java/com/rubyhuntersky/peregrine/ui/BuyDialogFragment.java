package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.columns.ColumnUiView;
import com.rubyhuntersky.columnui.presentations.EmptyPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.peregrine.AssetPrice;
import com.rubyhuntersky.peregrine.BuyProgram;
import com.rubyhuntersky.peregrine.FundingAccount;
import com.rubyhuntersky.peregrine.FundingOption;
import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.TradeDialogFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.rubyhuntersky.columnui.Creator.colorColumn;
import static com.rubyhuntersky.columnui.Creator.gapColumn;
import static com.rubyhuntersky.columnui.Creator.textColumn;
import static com.rubyhuntersky.columnui.Creator.textTile;
import static com.rubyhuntersky.columnui.basics.Coloret.BLACK;
import static com.rubyhuntersky.columnui.basics.Coloret.WHITE;
import static com.rubyhuntersky.columnui.basics.Sizelet.FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.PREVIOUS;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.READABLE;
import static com.rubyhuntersky.columnui.basics.Sizelet.THIRD_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.TWO_THIRDS_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.ofPortion;
import static com.rubyhuntersky.columnui.basics.TextStylet.IMPORTANT_DARK;
import static com.rubyhuntersky.columnui.basics.TextStylet.READABLE_DARK;
import static com.rubyhuntersky.columnui.material.Android.spinnerBar;
import static com.rubyhuntersky.columnui.material.Android.spinnerColumn;
import static com.rubyhuntersky.columnui.material.Android.spinnerTile;
import static com.rubyhuntersky.columnui.tiles.TileCreator.textTile1;
import static com.rubyhuntersky.peregrine.ui.UiHelper.getCurrencyDisplayString;

/**
 * @author wehjin
 * @since 1/19/16.
 */
public class BuyDialogFragment extends TradeDialogFragment {

    public static final String DIVISION_SIGN = "\u00f7";
    public static final ColumnUi SPACING = gapColumn(Sizelet.QUARTER_FINGER);
    public static final ColumnUi DOUBLE_SPACING = gapColumn(Sizelet.HALF_FINGER);
    public static final ColumnUi DIVIDER = colorColumn(ofPortion(.1f, READABLE), BLACK);
    public static final String TAG = BuyDialogFragment.class.getSimpleName();
    public static final String PROGRAM_KEY = "programKey";

    private ColumnUiView columnUiView;
    private Presentation presentation = new EmptyPresentation();
    private ColumnUi ui;
    private BuyProgram program;

    public static BuyDialogFragment create(BigDecimal amount, List<AssetPrice> prices, int selectedPrice, List<FundingAccount> fundingAccounts) {
        final BuyProgram buyProgram = new BuyProgram(amount, prices, selectedPrice);
        if (fundingAccounts != null) {
            buyProgram.setFundingAccounts(fundingAccounts, 0, 0);
        }

        final Bundle arguments = new Bundle();
        arguments.putParcelable(PROGRAM_KEY, buyProgram);

        final BuyDialogFragment fragment = new BuyDialogFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PROGRAM_KEY, program);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

        program = getArguments().getParcelable(PROGRAM_KEY);
        if (program == null) {
            return;
        }

        final String buyString = "Buy " + getCurrencyDisplayString(program.buyAmount);
        final List<String> symbols = new ArrayList<>();
        for (AssetPrice price : program.buyOptions) {
            symbols.add(price.name + " " + getCurrencyDisplayString(price.price));
        }

        final TileUi divisionSign = textTile(DIVISION_SIGN, IMPORTANT_DARK);

        final ColumnUi2<Integer, String> purchaseUi = textColumn(buyString, READABLE_DARK)
              .expandBottom(SPACING)
              .expandBottom(spinnerBar(symbols).expandStart(divisionSign).toColumn(FINGER))
              .expandBottom(SPACING)
              .expandBottom(DIVIDER)
              .expandBottom(SPACING)
              .expandBottom(textTile1(IMPORTANT_DARK).toColumn());

        List<String> fundingAccountNames = new ArrayList<>();
        final List<? extends FundingAccount> fundingAccounts = program.getFundingAccounts();
        for (FundingAccount fundingAccount : fundingAccounts) {
            fundingAccountNames.add("Account " + fundingAccount.getAccountName());
        }
        int selectedFundingAccount = program.getSelectedFundingAccount();


        final BigDecimal fundsNeededToBuy = program.getAdditionalFundsNeededToBuy();

        final List<FundingOption> fundingOptions = program.getFundingOptions();
        List<String> fundingOptionPrices = new ArrayList<>();
        for (FundingOption fundingOption : fundingOptions) {
            fundingOptionPrices.add(fundingOption.getAssetName() + " " + getCurrencyDisplayString(fundingOption.getSellPrice()));
        }
        int selectedFundingOption = program.getSelectedFundingOption();

        final String addFunds = fundsNeededToBuy.equals(BigDecimal.ZERO)
              ? "Sufficient funds"
              : "Add Funds " + getCurrencyDisplayString(fundsNeededToBuy);

        final ColumnUi sellUi =
              spinnerTile(fundingOptionPrices, selectedFundingOption).expandLeft(divisionSign).toColumn()
                    .expandBottom(SPACING)
                    .expandBottom(DIVIDER)
                    .expandBottom(SPACING)
                    .expandBottom(textColumn("Sell " + program.getSharesToSellForFunding()
                          .setScale(0, BigDecimal.ROUND_CEILING) + " shares", IMPORTANT_DARK));

        final ColumnUi fundingUi = spinnerColumn(fundingAccountNames, selectedFundingAccount)
              .expandBottom(DOUBLE_SPACING)
              .expandBottom(textColumn(addFunds, READABLE_DARK))
              .expandBottom(DOUBLE_SPACING)
              .expandBottom(sellUi)
              .isolate();

        final ColumnUi2<Integer, String> contentUi = purchaseUi.expandBottom(gapColumn(Sizelet.FINGER))
              .expandBottom(fundingUi);

        ui = contentUi.expandVertical(TWO_THIRDS_FINGER)
              .padHorizontal(THIRD_FINGER)
              .placeBefore(colorColumn(PREVIOUS, WHITE), 0)
              .printReadEval(new ColumnUi2.Repl<Integer, String>() {

                  private int selection = program.getSelectedBuyOption();

                  @Override
                  public ColumnUi print(ColumnUi2<Integer, String> ui2) {
                      final String sharesString = getSharesString(program.getSharesToBuy());
                      return ui2.bind(program.getSelectedBuyOption()).bind(sharesString);
                  }

                  @Override
                  public void read(Reaction reaction) {
                      if (reaction instanceof ItemSelectionReaction) {
                          selection = (int) ((ItemSelectionReaction) reaction).getItem();
                      }
                  }

                  @Override
                  public boolean eval() {
                      if (selection == program.getSelectedBuyOption())
                          return false;
                      program.setSelectedBuyOption(selection);
                      return true;
                  }
              });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_buy, container, false);
        columnUiView = (ColumnUiView) inflate.findViewById(R.id.ui);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        present();
    }

    private void present() {
        presentation.cancel();
        presentation = columnUiView.present(ui, new Observer() {
            @Override
            public void onReaction(Reaction reaction) {
                Log.d(TAG, "onReaction: " + reaction);
            }

            @Override
            public void onEnd() {
                Log.d(TAG, "onEnd");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError", throwable);
            }
        });
    }

    @Override
    public void onPause() {
        presentation.cancel();
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
