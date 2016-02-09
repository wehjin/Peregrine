package com.rubyhuntersky.columnui.shapes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.rubyhuntersky.columnui.Observer;
import com.rubyhuntersky.columnui.reactions.ItemSelectionReaction;

import java.util.List;

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
