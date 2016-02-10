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
import com.rubyhuntersky.columnui.columns.ColumnUi4;
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
import static com.rubyhuntersky.columnui.basics.Sizelet.PREVIOUS;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.READABLE;
import static com.rubyhuntersky.columnui.basics.Sizelet.THIRD_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.TWO_THIRDS_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.ofPortion;
import static com.rubyhuntersky.columnui.basics.TextStylet.IMPORTANT_DARK;
import static com.rubyhuntersky.columnui.basics.TextStylet.READABLE_DARK;
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
    public static final ColumnUi DIVIDER = colorColumn(ofPortion(.1f, READABLE), BLACK);
    public static final String TAG = BuyDialogFragment.class.getSimpleName();
    public static final String PROGRAM_KEY = "programKey";
    public static final TileUi DIVISION_SIGN_TILE = textTile(DIVISION_SIGN, IMPORTANT_DARK);
    public static final String BUY_PRICES_SPINNER = "buyPricesSpinner";
    public static final String FUNDING_ACCOUNT_SPINNER = "fundingAccountSpinner";

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

        ui = getBuyUi()
              .expandBottom(gapColumn(Sizelet.FINGER))
              .expandBottom(getFundingUi())
              .expandVertical(TWO_THIRDS_FINGER)
              .padHorizontal(THIRD_FINGER)
              .placeBefore(colorColumn(PREVIOUS, WHITE), 0)
              .printReadEval(new ColumnUi4.Repl<Integer, String, Integer, String>() {

                  private int fundingAccountSelection = program.getSelectedFundingAccount();
                  private int buyPriceSelection = program.getSelectedBuyOption();

                  @Override
                  public ColumnUi print(ColumnUi4<Integer, String, Integer, String> div4) {
                      return div4.bind(program.getSelectedBuyOption())
                            .bind(getSharesString(program.getSharesToBuy()))
                            .bind(program.getSelectedFundingAccount())
                            .bind(program.fundingAccountHasSufficientFundsToBuy()
                                        ? "Sufficient funds " + getCurrencyDisplayString(program.getFundingAccount()
                                                                                               .getCashAvailable())
                                        : "Add funds " + getCurrencyDisplayString(program.getAdditionalFundsNeededToBuy()));
                  }

                  @Override
                  public void read(Reaction reaction) {
                      if (reaction.getSource().equals(BUY_PRICES_SPINNER)) {
                          buyPriceSelection = (int) ((ItemSelectionReaction) reaction).getItem();
                      }
                      if (reaction.getSource().equals(FUNDING_ACCOUNT_SPINNER)) {
                          fundingAccountSelection = (int) ((ItemSelectionReaction) reaction).getItem();
                      }
                  }

                  @Override
                  public boolean eval() {
                      if (buyPriceSelection == program.getSelectedBuyOption()
                            && fundingAccountSelection == program.getSelectedFundingAccount())
                          return false;

                      program.setSelectedBuyOption(buyPriceSelection);
                      program.setSelectedFundingAccount(fundingAccountSelection);
                      return true;
                  }
              });

    }

    private ColumnUi2<Integer, String> getBuyUi() {
        final List<String> buyPrices = new ArrayList<>();
        for (AssetPrice price : program.buyOptions) {
            buyPrices.add(price.name + " " + getCurrencyDisplayString(price.price));
        }

        return textColumn("Buy " + getCurrencyDisplayString(program.buyAmount), READABLE_DARK)
              .expandBottom(SPACING)
              .expandBottom(spinnerTile(buyPrices)
                                  .name(BUY_PRICES_SPINNER)
                                  .expandLeft(DIVISION_SIGN_TILE)
                                  .toColumn())
              .expandBottom(SPACING)
              .expandBottom(DIVIDER)
              .expandBottom(SPACING)
              .expandBottom(textTile1(IMPORTANT_DARK).toColumn());
    }

    private ColumnUi2<Integer, String> getFundingUi() {
        List<String> fundingAccountNames = new ArrayList<>();
        for (FundingAccount fundingAccount : program.getFundingAccounts()) {
            fundingAccountNames.add("Account " + fundingAccount.getAccountName());
        }

        return spinnerTile(fundingAccountNames).name(FUNDING_ACCOUNT_SPINNER).toColumn()
              .expandBottom(textTile1(READABLE_DARK).toColumn())
              .expandBottom(SPACING)
              .expandBottom(getSellUi());
    }

    private ColumnUi getSellUi() {
        final List<FundingOption> fundingOptions = program.getFundingOptions();
        List<String> fundingOptionPrices = new ArrayList<>();
        for (FundingOption fundingOption : fundingOptions) {
            fundingOptionPrices.add(fundingOption.getAssetName() + " " + getCurrencyDisplayString(fundingOption.getSellPrice()));
        }
        int selectedFundingOption = program.getSelectedFundingOption();

        return spinnerTile(fundingOptionPrices, selectedFundingOption).expandLeft(DIVISION_SIGN_TILE).toColumn()
              .expandBottom(SPACING)
              .expandBottom(DIVIDER)
              .expandBottom(SPACING)
              .expandBottom(textColumn("Sell " + program.getSharesToSellForFunding()
                    .setScale(0, BigDecimal.ROUND_CEILING) + " shares", IMPORTANT_DARK));
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
