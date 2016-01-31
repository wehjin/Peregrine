package com.rubyhuntersky.columnui.material;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.bars.BarUi;
import com.rubyhuntersky.columnui.bars.BarUi1;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.presentations.PatchPresentation;
import com.rubyhuntersky.columnui.presenters.OnPresent;
import com.rubyhuntersky.columnui.presenters.Presenter;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.shapes.ViewShape;
import com.rubyhuntersky.columnui.tiles.Tile;
import com.rubyhuntersky.columnui.tiles.TileUi;

import java.util.List;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public class Android {

    public static TileUi spinnerTile(final List<String> options, final int selectedOption) {
        return TileUi.create(new OnPresent<Tile>() {
            @Override
            public void onPresent(Presenter<Tile> presenter) {
                final Tile tile = presenter.getDisplay();
                final SpinnerViewShape spinnerViewShape = new SpinnerViewShape(options, selectedOption, Observer.EMPTY);
                final ShapeSize shapeSize = tile.measureShape(spinnerViewShape);
                final Frame frame = new Frame(shapeSize.measuredWidth, shapeSize.measuredHeight, tile.elevation);
                final SpinnerViewShape shape = new SpinnerViewShape(options, selectedOption, presenter);
                final Patch patch = tile.addPatch(frame, shape);
                presenter.addPresentation(new PatchPresentation(patch, frame));
            }
        });
    }

    public static BarUi spinnerBar(final List<String> options, final int selectedOption) {
        return spinnerTile(options, selectedOption).toBar();
    }

    public static BarUi1<Integer> spinnerBar(final List<String> options) {
        return BarUi1.create(new BarUi1.OnBind<Integer>() {
            @Override
            public BarUi onBind(Integer condition) {
                return spinnerBar(options, condition);
            }
        });
    }

    private static class SpinnerViewShape extends ViewShape {
        private final List<String> options;
        private final int selectedOption;
        private final Observer observer;

        public SpinnerViewShape(List<String> options, int selectedOption, @NonNull Observer observer) {
            this.options = options;
            this.selectedOption = selectedOption;
            this.observer = observer;
        }

        @Override
        public View createView(Context context) {
            final int spinnerRes = android.R.layout.simple_spinner_item;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, spinnerRes, options) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    return super.getView(position, convertView, parent);
                }
            };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            final Spinner spinner = new Spinner(context);
            spinner.setAdapter(adapter);
            spinner.setSelection(selectedOption);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                private int lastPosition = selectedOption;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == lastPosition) {
                        return;
                    }
                    lastPosition = position;
                    observer.onReaction(new ItemSelectionReaction<>(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if (lastPosition < 0) {
                        return;
                    }
                    lastPosition = -1;
                    observer.onReaction(new ItemSelectionReaction<>(-1));
                }
            });
            final FrameLayout frameLayout = new FrameLayout(context);
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                  LayoutParams.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL);
            frameLayout.addView(spinner, params);
            return frameLayout;
        }

    }
}
