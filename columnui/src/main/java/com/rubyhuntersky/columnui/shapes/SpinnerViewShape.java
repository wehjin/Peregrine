package com.rubyhuntersky.columnui.shapes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.rubyhuntersky.columnui.Creator;
import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.Shape;
import com.rubyhuntersky.columnui.basics.Frame;
import com.rubyhuntersky.columnui.basics.ShapeSize;
import com.rubyhuntersky.columnui.basics.TextSize;
import com.rubyhuntersky.columnui.basics.TextStyle;
import com.rubyhuntersky.columnui.basics.TextStylet;
import com.rubyhuntersky.columnui.patches.Patch;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.tiles.FullTile;
import com.rubyhuntersky.columnui.tiles.TileUi;
import com.rubyhuntersky.columnui.ui.ShapeDisplayView;

import java.util.List;

import static com.rubyhuntersky.columnui.basics.Sizelet.READABLE;

/**
 * @author wehjin
 * @since 2/9/16.
 */
public class SpinnerViewShape extends ViewShape {
    private final List<String> options;
    private final int selectedOption;
    private final Observer observer;

    public SpinnerViewShape(List<String> options, int selectedOption, @NonNull Observer observer) {
        this.options = options;
        this.selectedOption = selectedOption;
        this.observer = observer;
    }

    @Override
    public View createView(final Context context) {

        BaseAdapter customAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return options.size();
            }

            @Override
            public String getItem(int position) {
                return options.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final TileUi tileUi = Creator.textTile(getItem(position), TextStylet.READABLE_DARK)
                      .expandHorizontal(READABLE).expandVertical(READABLE);
                final FullTile fullTile = new FullTile() {

                    private ShapeDisplayView shapeDisplayView = new ShapeDisplayView(context);

                    @NonNull
                    @Override
                    public Patch addPatch(Frame frame, Shape shape) {
                        return shapeDisplayView.addPatch(frame, shape);
                    }

                    @NonNull
                    @Override
                    public TextSize measureText(String text, TextStyle textStyle) {
                        return shapeDisplayView.measureText(text, textStyle);
                    }

                    @NonNull
                    @Override
                    public ShapeSize measureShape(Shape shape) {
                        return shapeDisplayView.measureShape(shape);
                    }
                };
                return null;
            }
        };


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
        //spinner.setPadding(50, 50, 50, 50);
        //spinner.setBackgroundColor(Color.YELLOW);
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
