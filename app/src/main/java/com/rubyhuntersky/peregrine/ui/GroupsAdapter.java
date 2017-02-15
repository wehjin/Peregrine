package com.rubyhuntersky.peregrine.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.model.Group;
import com.rubyhuntersky.peregrine.utility.ExtensionsKt;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jeffrey Yu
 * @since 2/15/17.
 */
class GroupsAdapter extends BaseAdapter {

    private final Context context;
    private final List<Group> groups;
    private final BigDecimal fullValue;

    GroupsAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
        fullValue = ExtensionsKt.getFullValue(groups);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return groups.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = convertView == null ? View.inflate(context, R.layout.cell_group, null) : convertView;
        final Group group = groups.get(position);

        final TextView startText = (TextView) view.findViewById(R.id.startText);
        final TextView startDetailText = (TextView) view.findViewById(R.id.startDetailText);
        final String name = group.getName();
        startText.setText(name);
        startDetailText.setText(UiHelper.getCurrencyDisplayString(group.getValue()));

        final TextView endText = (TextView) view.findViewById(R.id.endText);
        final TextView endDetailText = (TextView) view.findViewById(R.id.endDetailText);

        String errorLabelString;
        String errorValueString;
        if (fullValue.equals(BigDecimal.ZERO)) {
            errorLabelString = "Zero";
            errorValueString = "-";
        } else {
            BigDecimal allocationError = group.getAllocationError(fullValue);
            if (allocationError.equals(BigDecimal.ZERO)) {
                errorLabelString = "Even";
                errorValueString = "Hold";
            } else {
                errorLabelString = allocationError.compareTo(BigDecimal.ZERO) > 0 ? "Over" : "Under";
                errorValueString = getDollarError(allocationError, fullValue);
            }
        }
        endText.setText(errorLabelString);
        endDetailText.setText(errorValueString);
        return view;
    }

    @NonNull
    private String getDollarError(BigDecimal allocationError, BigDecimal fullValue) {
        final BigDecimal dollarError = allocationError.multiply(fullValue);
        final int versusZero = dollarError.compareTo(BigDecimal.ZERO);
        if (versusZero > 0) {
            return "Sell " + UiHelper.getCurrencyDisplayString(dollarError);
        } else if (versusZero < 0) {
            return "Buy " + UiHelper.getCurrencyDisplayString(dollarError.abs());
        } else {
            return "Hold";
        }
    }
}
