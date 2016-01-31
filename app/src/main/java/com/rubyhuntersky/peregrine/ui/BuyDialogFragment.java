package com.rubyhuntersky.peregrine.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Reaction;
import com.rubyhuntersky.columnui.bars.BarUi1;
import com.rubyhuntersky.columnui.basics.Sizelet;
import com.rubyhuntersky.columnui.columns.ColumnUi;
import com.rubyhuntersky.columnui.columns.ColumnUi2;
import com.rubyhuntersky.columnui.columns.ColumnUiView;
import com.rubyhuntersky.columnui.presentations.EmptyPresentation;
import com.rubyhuntersky.columnui.presentations.Presentation;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.tiles.TileCreator;
import com.rubyhuntersky.columnui.tiles.TileUi1;
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
import static com.rubyhuntersky.columnui.basics.Sizelet.PREVIOUS;
import static com.rubyhuntersky.columnui.basics.Sizelet.Ruler.READABLE;
import static com.rubyhuntersky.columnui.basics.Sizelet.THIRD_FINGER;
import static com.rubyhuntersky.columnui.basics.Sizelet.TWO_THIRDS_FINGER;
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
    public static final ColumnUi SPACING = colorColumn(Sizelet.QUARTER_FINGER, null);
    public static final ColumnUi DIVIDER = colorColumn(ofPortion(.1f, READABLE), BLACK);
    public static final String TAG = BuyDialogFragment.class.getSimpleName();

    private BigDecimal buyAmount = BigDecimal.ZERO;
    private List<AssetPrice> prices;

    private ColumnUiView columnUiView;
    private Presentation presentation = new EmptyPresentation();
    private AssetPrice selectedAssetPrice;
    private ColumnUi program;

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
        selectedAssetPrice = getArguments().getParcelable(SELECTED_PRICE_KEY);
        final String buyString = "Buy " + UiHelper.getCurrencyDisplayString(buyAmount);
        final List<String> symbols = new ArrayList<>();
        for (AssetPrice price : prices) {
            symbols.add(price.name + " " + UiHelper.getCurrencyDisplayString(price.amount));
        }
        final ColumnUi amountColumn = textColumn(buyString, IMPORTANT_DARK);
        final BarUi1<Integer> pricesBar = spinnerBar(symbols).expandStart(textTile(DIVISION_SIGN, IMPORTANT_DARK));
        final TileUi1<String> sharedTile = TileCreator.textTile1(IMPORTANT_DARK);
        program = amountColumn.expandBottom(SPACING)
                              .expandBottom(pricesBar.toColumn(FINGER))
                              .expandBottom(SPACING)
                              .expandBottom(DIVIDER)
                              .expandBottom(SPACING)
                              .expandBottom(sharedTile.toColumn())
                              .expandVertical(TWO_THIRDS_FINGER)
                              .padHorizontal(THIRD_FINGER)
                              .placeBefore(colorColumn(PREVIOUS, WHITE), 0)
                              .printReadEval(new ColumnUi2.Repl<Integer, String>() {

                                  private int selectedSymbol = prices.indexOf(selectedAssetPrice);
                                  private int newSelectedSymbol = selectedSymbol;

                                  @Override
                                  public ColumnUi print(ColumnUi2<Integer, String> ui2) {
                                      return ui2.bind(selectedAssetPrice.getSharesString(buyAmount))
                                                .bind(selectedSymbol);
                                  }

                                  @Override
                                  public void read(Reaction reaction) {
                                      if (reaction instanceof ItemSelectionReaction) {
                                          newSelectedSymbol = (int) ((ItemSelectionReaction) reaction).getItem();
                                      }
                                  }

                                  @Override
                                  public boolean eval() {
                                      if (newSelectedSymbol == selectedSymbol) return false;
                                      selectedAssetPrice = prices.get(newSelectedSymbol);
                                      selectedSymbol = newSelectedSymbol;
                                      return true;
                                  }
                              });

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
        present();
    }

    private void present() {
        presentation.cancel();
        presentation = columnUiView.present(program, new Observer() {
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
