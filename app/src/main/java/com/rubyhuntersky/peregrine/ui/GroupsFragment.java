package com.rubyhuntersky.peregrine.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.Asset;
import com.rubyhuntersky.peregrine.Assignments;
import com.rubyhuntersky.peregrine.Group;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.PortfolioAssets;
import com.rubyhuntersky.peregrine.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func3;

import static com.rubyhuntersky.peregrine.ui.SellDialogFragment.Price;

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
              getBaseActivity().getPortfolioAssetsStream(), getStorage().streamAssignments(),
              new Func3<PartitionList, PortfolioAssets, Assignments, Document>() {
                  @Override
                  public Document call(PartitionList partitionList, PortfolioAssets portfolioAssets,
                        Assignments assignments) {
                      return new Document(partitionList, portfolioAssets, assignments);
                  }
              }).subscribe(new Action1<Document>() {
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
            showList(groups);
        }
    }

    private void showList(List<Group> groups) {
        textView.setVisibility(View.GONE);

        final GroupsAdapter groupsAdapter = getGroupsAdapter(getActivity(), groups);
        listView.setAdapter(groupsAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Group group = groupsAdapter.getItem(position);
                final BigDecimal sellAmount = groupsAdapter.getAllocationErrorDollars(group);
                List<Price> prices = getPrices(group);
                Price selectedPrice = prices.get(0);
                if (sellAmount.compareTo(BigDecimal.ZERO) > 0) {
                    final DialogFragment fragment = SellDialogFragment.create(sellAmount, prices, selectedPrice);
                    fragment.show(getFragmentManager(), "SellFragment");
                }
            }
        });
    }

    @NonNull
    private List<Price> getPrices(Group group) {
        final List<Asset> assets = group.getAssets();
        List<Price> prices = new ArrayList<>();
        Set<String> symbols = new HashSet<>();
        for (Asset asset : assets) {
            if (symbols.contains(asset.symbol)) {
                continue;
            }
            prices.add(new Price(asset.symbol, asset.currentPrice));
            symbols.add(asset.symbol);
        }
        return prices;
    }

    private void showText(String message) {
        listView.setVisibility(View.GONE);
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }

    @NonNull
    private GroupsAdapter getGroupsAdapter(final Context context, final List<Group> groups) {
        return new GroupsAdapter(context, groups);
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

    private class GroupsAdapter extends BaseAdapter {

        private final Context context;
        private final List<Group> groups;
        private final BigDecimal fullValue;

        public GroupsAdapter(Context context, List<Group> groups) {
            this.context = context;
            this.groups = groups;
            fullValue = getFullValue(groups);
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

            String errorLabelString = "On";
            String errorValueString = "-";
            if (!fullValue.equals(BigDecimal.ZERO)) {
                BigDecimal allocationError = getAllocationError(group);
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

        private BigDecimal getAllocationError(Group group) {
            return group.getAllocationError(fullValue);
        }

        private BigDecimal getAllocationErrorDollars(Group group) {
            final BigDecimal allocationError = getAllocationError(group);
            return allocationError.multiply(fullValue);
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
}
