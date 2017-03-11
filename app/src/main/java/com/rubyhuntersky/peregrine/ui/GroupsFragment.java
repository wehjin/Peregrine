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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.R;
import com.rubyhuntersky.peregrine.model.AccountAssets;
import com.rubyhuntersky.peregrine.model.AccountSaleOption;
import com.rubyhuntersky.peregrine.model.Asset;
import com.rubyhuntersky.peregrine.model.AssetNamePrice;
import com.rubyhuntersky.peregrine.model.Assignments;
import com.rubyhuntersky.peregrine.model.FundingAccount;
import com.rubyhuntersky.peregrine.model.Group;
import com.rubyhuntersky.peregrine.model.GroupSaleOption;
import com.rubyhuntersky.peregrine.model.PartitionList;
import com.rubyhuntersky.peregrine.model.PortfolioAssets;
import com.rubyhuntersky.peregrine.utility.ExtensionsKt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kotlin.collections.CollectionsKt;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
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
                                     public Document call(PartitionList partitionList, PortfolioAssets portfolioAssets, Assignments assignments) {
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

    private BigDecimal getAllocationErrorDollars(Group group, BigDecimal fullValue) {
        return group.getAllocationError(fullValue).multiply(fullValue);
    }

    private void showList(final List<Group> groups) {
        textView.setVisibility(View.GONE);

        final GroupsAdapter groupsAdapter = getGroupsAdapter(getActivity(), groups);
        listView.setAdapter(groupsAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Group group = groupsAdapter.getItem(position);
                final BigDecimal fullValue = ExtensionsKt.getFullValue(groups);
                final BigDecimal sellAmount = getAllocationErrorDollars(group, fullValue);
                final int direction = sellAmount.compareTo(BigDecimal.ZERO);
                if (direction > 0) {
                    startSellDialog(group, sellAmount);
                } else if (direction < 0) {
                    startBuyDialog(group, sellAmount.abs());
                }
            }
        });
    }

    private void startSellDialog(Group group, final BigDecimal sellAmount) {
        final List<GroupSaleOption> saleOptions = getGroupSaleOptions(group);
        SellDialogFragment.create(sellAmount, saleOptions, 0).show(getFragmentManager(), "SellFragment");
    }

    @NonNull
    private List<GroupSaleOption> getGroupSaleOptions(Group group) {
        final List<AssetNamePrice> assetNamePrices = getPrices(group);
        final List<GroupSaleOption> saleOptions = new ArrayList<>(assetNamePrices.size());
        final Map<String, List<AccountSaleOption>> accountSaleOptions = getAccountSaleOptions(group);
        for (AssetNamePrice assetNamePrice : assetNamePrices) {
            final String symbol = assetNamePrice.getName();
            final List<AccountSaleOption> accountSaleOptionsForSymbol = accountSaleOptions.containsKey(symbol)
                  ? accountSaleOptions.get(symbol)
                  : Collections.<AccountSaleOption>emptyList();
            saleOptions.add(new GroupSaleOption(symbol, assetNamePrice.getPrice(), accountSaleOptionsForSymbol));
        }
        return saleOptions;
    }

    @NonNull
    private Map<String, List<AccountSaleOption>> getAccountSaleOptions(Group group) {
        final List<AssetNamePrice> prices = getPrices(group);

        final Set<String> targetAssetNames = new HashSet<>();
        for (AssetNamePrice assetNamePrice : prices) {
            targetAssetNames.add(assetNamePrice.getName());
        }

        final HashMap<String, List<AccountSaleOption>> accountOptions = new HashMap<>();
        for (Asset asset : group.getAssets()) {
            final String symbol = asset.getSymbol();
            if (targetAssetNames.contains(symbol)) {
                final AccountSaleOption accountSaleOption = new AccountSaleOption(asset.getAccountId(),
                                                                                  asset.getAccountDescription(),
                                                                                  symbol,
                                                                                  asset.getQuantity());
                final List<AccountSaleOption> options = accountOptions.containsKey(symbol)
                      ? accountOptions.get(symbol)
                      : CollectionsKt.<AccountSaleOption>mutableListOf();
                options.add(accountSaleOption);
                accountOptions.put(symbol, options);
            }
        }
        return accountOptions;
    }

    private void startBuyDialog(Group group, final BigDecimal buyAmount) {
        final List<AssetNamePrice> prices = getPrices(group);
        if (prices.size() == 0) {
            prices.add(new AssetNamePrice());
        }
        getBaseActivity().getAccountAssetsListStream().first()
              .map(new Func1<List<AccountAssets>, List<FundingAccount>>() {
                  @Override
                  public List<FundingAccount> call(List<AccountAssets> accountAssetsList) {
                      List<FundingAccount> fundingAccounts = new ArrayList<>();
                      for (AccountAssets accountAssets : accountAssetsList) {
                          fundingAccounts.add(accountAssets.toFundingAccount());
                      }
                      return fundingAccounts;
                  }
              })
              .subscribe(new Action1<List<FundingAccount>>() {
                  @Override
                  public void call(List<FundingAccount> fundingAccounts) {
                      final List<FundingAccount> accounts = fundingAccounts == null
                            ? Collections.<FundingAccount>emptyList()
                            : fundingAccounts;
                      BuyDialogFragment.create(buyAmount, prices, 0, accounts)
                            .show(getFragmentManager(), "BuyFragment");
                  }
              });
    }

    @NonNull
    private List<AssetNamePrice> getPrices(Group group) {
        final List<Asset> assets = group.getAssets();
        List<AssetNamePrice> prices = new ArrayList<>();
        Set<String> symbols = new HashSet<>();
        for (Asset asset : assets) {
            if (symbols.contains(asset.getSymbol())) {
                continue;
            }
            prices.add(new AssetNamePrice(asset.getSymbol(), asset.getCurrentPrice()));
            symbols.add(asset.getSymbol());
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
