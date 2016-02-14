package com.rubyhuntersky.columnui.shapes;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.tiles.Tile1;
import com.rubyhuntersky.columnui.tiles.TileView;

import java.util.List;

import static com.rubyhuntersky.columnui.basics.Sizelet.IMPORTANT;

/**
 * @author wehjin
 * @since 2/9/16.
 */
public class SpinnerViewShape<C> extends ViewShape {
    private final List<C> options;
    private final Tile1<C> optionsTile;
    private final int selectedOption;
    private final Observer observer;

    public SpinnerViewShape(List<C> options, Tile1<C> optionsTile, int selectedOption, Observer observer) {
        super();
        this.options = options;
        this.optionsTile = optionsTile;
        this.selectedOption = selectedOption;
        this.observer = observer;
    }

    @Override
    public View createView(final Context context) {


        final Spinner spinner = new Spinner(context);
        spinner.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return options.size();
            }

            @Override
            public C getItem(int position) {
                return options.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final C item = getItem(position);
                final TileView tileView = new TileView(context);
                tileView.present(optionsTile.bind(item).expandHorizontal(IMPORTANT).expandVertical(IMPORTANT));
                return tileView;
            }
        });
        spinner.setSelection(selectedOption);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private int lastPosition = selectedOption;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == lastPosition) {
                    return;
                }
                lastPosition = position;
                observer.onReaction(new ItemSelectionReaction<>("spinner", position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (lastPosition < 0) {
                    return;
                }
                lastPosition = -1;
                observer.onReaction(new ItemSelectionReaction<>("spinner", -1));
            }
        });
        final FrameLayout frameLayout = new FrameLayout(context);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                             ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                             Gravity.START | Gravity.CENTER_VERTICAL);
        frameLayout.addView(spinner, params);
        return frameLayout;
    }

}
