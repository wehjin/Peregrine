package com.rubyhuntersky.peregrine.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.Assignments;
import com.rubyhuntersky.peregrine.Group;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.PortfolioAssets;
import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func3;

public class GroupsFragment extends BaseFragment {

    private TextView textView;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assets, container, false);
        textView = (TextView) view.findViewById(R.id.text);
        listView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable.combineLatest(getBaseActivity().getPartitionListStream(),
                                 getBaseActivity().getPortfolioAssetsStream(),
                                 getStorage().streamAssignments(),
                                 new Func3<PartitionList, PortfolioAssets, Assignments, Document>() {
                                     @Override
                                     public Document call(PartitionList partitionList,
                                           PortfolioAssets portfolioAssets, Assignments assignments) {
                                         return new Document(partitionList, portfolioAssets, assignments);
                                     }
                                 })
                  .subscribe(new Action1<Document>() {
                      @Override
                      public void call(Document document) {
                          updateViews(document);
                      }
                  }, getErrorAction());
    }

    @Override
    protected Action1<Throwable> getErrorAction() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error", throwable);
                showText(throwable.getLocalizedMessage());
            }
        };
    }

    private void updateViews(final Document document) {
        final List<Group> groups = document.getGroups();
        if (groups.isEmpty()) {
            showText("No data");
        } else {
            showList(getGroupsBaseAdapter(getActivity(), groups));
        }
    }

    private void showList(ListAdapter adapter) {
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    private void showText(String message) {
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    @NonNull
    private BaseAdapter getGroupsBaseAdapter(final Context context, final List<Group> groups) {
        final BigDecimal fullValue = getFullValue(groups);
        Log.d(TAG, "Full value: " + getCurrencyDisplayString(fullValue));
        return new BaseAdapter() {

            @Override
            public int getCount() {
                return groups.size();
            }

            @Override
            public Object getItem(int position) {
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
                startDetailText.setText(getCurrencyDisplayString(group.getValue()));

                final TextView endText = (TextView) view.findViewById(R.id.endText);
                final TextView endDetailText = (TextView) view.findViewById(R.id.endDetailText);

                String errorLabelString = "On";
                String errorValueString = "-";
                if (!fullValue.equals(BigDecimal.ZERO)) {
                    BigDecimal currentAllocation = group.getCurrentAllocation(fullValue);
                    BigDecimal targetAllocation = group.getTargetAllocation();
                    Log.d(TAG,
                          name + ":" + currentAllocation.toPlainString() + " current, " + targetAllocation
                                .toPlainString() + " target");
                    BigDecimal allocationError = currentAllocation.subtract(targetAllocation);
                    if (allocationError.equals(BigDecimal.ZERO)) {
                        errorLabelString = "Even";
                        errorValueString = "Hold";
                    } else {
                        errorLabelString = allocationError.doubleValue() > 0 ? "Over" : "Under";
                        errorValueString = getDollarError(allocationError, fullValue);
                    }
                }
                endText.setText(errorLabelString);
                endDetailText.setText(errorValueString);
                return view;
            }
        };
    }

    private BigDecimal getPercentError(BigDecimal allocationError, BigDecimal targetAllocation) {
        if (targetAllocation.equals(BigDecimal.ZERO)) {
            return null;
        }

        return allocationError.divide(targetAllocation, BigDecimal.ROUND_HALF_UP);
    }


    private String getPercentErrorString(BigDecimal allocationError, BigDecimal targetAllocation) {
        final BigDecimal percentError = getPercentError(allocationError, targetAllocation);
        if (percentError == null) {
            return "inf";
        }
        return String.format("%.2f %%", percentError.abs().doubleValue() * 100);
    }

    @NonNull
    private String getDollarError(BigDecimal allocationError, BigDecimal fullValue) {
        final BigDecimal dollarError = allocationError.multiply(fullValue);
        final int versusZero = dollarError.compareTo(BigDecimal.ZERO);
        if (versusZero > 0) {
            return "Sell " + getCurrencyDisplayString(dollarError);
        } else if (versusZero < 0) {
            return "Buy " + getCurrencyDisplayString(dollarError.abs());
        } else {
            return "Hold";
        }
    }

    private BigDecimal getFullValue(List<Group> groups) {
        BigDecimal fullValue = BigDecimal.ZERO;
        for (Group group : groups) {
            fullValue = fullValue.add(group.getValue());
        }
        return fullValue;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assets, menu);
    }


    private static class Document {
        public final PartitionList partitionList;
        private final PortfolioAssets portfolioAssets;
        public final Assignments assignments;

        public Document(PartitionList partitionList, PortfolioAssets portfolioAssets, Assignments assignments) {
            this.partitionList = partitionList;
            this.portfolioAssets = portfolioAssets;
            this.assignments = assignments;
        }

        public List<Group> getGroups() {
            return partitionList.getGroups(portfolioAssets, assignments);
        }
    }
}
