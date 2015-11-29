package com.rubyhuntersky.peregrine.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.AccountAssets;
import com.rubyhuntersky.peregrine.Asset;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

public class AssetsFragment extends BaseFragment {

    public static final Func2<PartitionList, List<AccountAssets>, Pair<PartitionList, List<AccountAssets>>>
          COMBINE_PARITION_LIST_AND_ACCOUNT_ASSETS_LIST = new Func2<PartitionList, List<AccountAssets>,
          Pair<PartitionList,
                List<AccountAssets>>>() {
        @Override
        public Pair<PartitionList, List<AccountAssets>> call(PartitionList partitionList,
              List<AccountAssets> accountAssetsList) {
            return new Pair<>(partitionList, accountAssetsList);
        }
    };
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
                                 getBaseActivity().getAccountAssetsListStream(),
                                 COMBINE_PARITION_LIST_AND_ACCOUNT_ASSETS_LIST)
                  .subscribe(new Action1<Pair<PartitionList, List<AccountAssets>>>() {
                                 @Override
                                 public void call(Pair<PartitionList, List<AccountAssets>> pair) {
                                     updateViews(pair.first, pair.second);
                                 }
                             },
                             getErrorAction());
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

    private void updateViews(final PartitionList partitionList, List<AccountAssets> accountAssetsList) {
        final List<Asset> assets = getAssets(accountAssetsList);
        if (assets.isEmpty()) {
            showText("No data");
        } else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String[] partitionNames = partitionList.toNamesArray("None");
                    final int startingIndex = 0;
                    final int[] endingIndex = {0};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                          .setTitle("Assign Group")
                          .setSingleChoiceItems(partitionNames, startingIndex, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  endingIndex[0] = which;
                              }
                          })
                          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  // Do nothing
                              }
                          })
                          .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  if (endingIndex[0] == startingIndex) {
                                      return;
                                  }
                              }
                          });
                    builder.show();
                }
            });
            showList(getAssetsBaseAdapter(getActivity(), partitionList, assets));
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
    private BaseAdapter getAssetsBaseAdapter(final Context context, final PartitionList partitionList,
          final List<Asset> assets) {
        return new BaseAdapter() {

            @Override
            public int getCount() {
                return assets.size();
            }

            @Override
            public Object getItem(int position) {
                return assets.get(position);
            }

            @Override
            public long getItemId(int position) {
                return assets.hashCode();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = convertView == null ? View.inflate(context, R.layout.cell_asset, null) : convertView;
                final Asset asset = assets.get(position);
                final TextView startText = (TextView) view.findViewById(R.id.startText);
                final TextView startDetailText = (TextView) view.findViewById(R.id.startDetailText);
                final TextView endText = (TextView) view.findViewById(R.id.endText);
                startText.setText(asset.symbol);
                startDetailText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                startDetailText.setText(getString(R.string.unassigned));
                endText.setText(getCurrencyDisplayString(asset.marketValue));
                return view;
            }
        };
    }

    @NonNull
    private List<Asset> getAssets(List<AccountAssets> accountAssetsList) {
        final List<Asset> assets = new ArrayList<>();
        if (accountAssetsList != null) {
            for (AccountAssets accountAssets : accountAssetsList) {
                assets.addAll(accountAssets.toAssetList());
            }
        }
        return assets;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assets, menu);
    }


}
