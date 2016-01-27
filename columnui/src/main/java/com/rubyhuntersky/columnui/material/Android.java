package com.rubyhuntersky.columnui.material;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rubyhuntersky.columnui.Frame;
import com.rubyhuntersky.columnui.OnPresent;
import com.rubyhuntersky.columnui.Patch;
import com.rubyhuntersky.columnui.Presenter;
import com.rubyhuntersky.columnui.Range;
import com.rubyhuntersky.columnui.Sizelet;
import com.rubyhuntersky.columnui.Ui;
import com.rubyhuntersky.columnui.conditions.Column;
import com.rubyhuntersky.columnui.presentations.BooleanPresentation;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;
import com.rubyhuntersky.columnui.shapes.ViewShape;

import java.util.List;

/**
 * @author wehjin
 * @since 1/27/16.
 */

public class Android {

    public static Ui createSpinner(final List<String> options, final String selectedOption) {
        return Ui.create(new OnPresent() {

            @Override
            public void onPresent(final Presenter presenter) {
                final Column column = presenter.getColumn();
                final float height = Sizelet.FINGER.toFloat(presenter.getHuman(), column.verticalRange.toLength());
                final ViewShape viewShape = new ViewShape() {
                    @Override
                    public View createView(Context context) {
                        final int spinnerRes = android.R.layout.simple_spinner_item;
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, spinnerRes, options) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                final TextView view = (TextView) super.getView(position, convertView, parent);
                                view.setTextSize(height / 6);
                                return view;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        final Spinner spinner = new Spinner(context);
                        spinner.setAdapter(adapter);
                        if (selectedOption != null) {
                            spinner.setSelection(options.indexOf(selectedOption));
                        }
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                presenter.onReaction(new ItemSelectionReaction<>(options.get(position)));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                presenter.onReaction(new ItemSelectionReaction<>(null));
                            }
                        });
                        final FrameLayout frameLayout = new FrameLayout(context);
                        frameLayout.addView(spinner, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                        return frameLayout;
                    }

                };
                final Range verticalRange = Range.of(0, height);
                final Frame frame = new Frame(column.horizontalRange, verticalRange, column.elevation);
                final Patch patch = column.addPatch(frame, viewShape);
                presenter.addPresentation(new BooleanPresentation() {
                    @Override
                    protected void onCancel() {
                        patch.remove();
                    }

                    @Override
                    public Range getVerticalRange() {
                        return verticalRange;
                    }
                });
            }
        });
    }
}
