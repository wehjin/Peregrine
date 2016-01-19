package com.rubyhuntersky.peregrine.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import com.rubyhuntersky.peregrine.Asset;
import com.rubyhuntersky.peregrine.Assignments;
import com.rubyhuntersky.peregrine.Partition;
import com.rubyhuntersky.peregrine.PartitionList;
import com.rubyhuntersky.peregrine.PortfolioAssets;
import com.rubyhuntersky.peregrine.R;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func3;

public class AssetsFragment extends BaseFragment {

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
                                 new Func3<PartitionList, PortfolioAssets, Assignments, ViewsData>() {
                                     @Override
                                     public ViewsData call(PartitionList partitionList,
                                           PortfolioAssets portfolioAssets, Assignments assignments) {
                                         return new ViewsData(partitionList, portfolioAssets, assignments);
                                     }
                                 })
                  .subscribe(new Action1<ViewsData>() {
                      @Override
                      public void call(ViewsData viewsData) {
                          updateViews(viewsData);
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

    private void updateViews(final ViewsData viewsData) {
        final PartitionList partitionList = viewsData.partitionList;
        final Assignments assignments = viewsData.assignments;
        final List<Asset> assets = viewsData.portfolioAssets.getAssets();
        if (assets.isEmpty()) {
            showText("No data");
        } else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showAssignmentDialog(assets.get(position), partitionList, assignments);
                }
            });
            showList(getAssetsBaseAdapter(getActivity(), viewsData.partitionList, assets, viewsData.assignments));
        }
    }

    private void showAssignmentDialog(final Asset asset, final PartitionList partitionList,
          final Assignments assignments) {
        final int startingIndex = 1 + getPartitionIndex(asset, partitionList, assignments);
        final int[] endingIndex = {startingIndex};
        new AlertDialog.Builder(getActivity())
              .setTitle("Assign Group")
              .setSingleChoiceItems(partitionList.toNamesArray("None"), startingIndex,
                                    new DialogInterface.OnClickListener() {
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
                      final int nextIndex = endingIndex[0];
                      if (nextIndex == startingIndex) {
                          return;
                      }
                      final String symbol = asset.symbol;
                      final List<Partition> partitions = partitionList.partitions;
                      Assignments nextAssignments = nextIndex == 0 ?
                            assignments.erasePartitionId(symbol) :
                            assignments.setPartitionId(symbol, partitions.get(nextIndex - 1).id);
                      getStorage().writeAssignments(nextAssignments);
                  }
              }).show();
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
          final List<Asset> assets, final Assignments assignments) {
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

                String partitionName = getPartitionName(asset, partitionList, assignments);
                final int detailColorRes = partitionName == null ? R.color.colorAccent : R.color.darkTextSecondary;
                startDetailText.setTextColor(ContextCompat.getColor(getActivity(), detailColorRes));
                startDetailText.setText(partitionName == null ? getString(R.string.unassigned) : partitionName);

                endText.setText(UiHelper.getCurrencyDisplayString(asset.marketValue));
                return view;
            }
        };
    }

    private String getPartitionName(Asset asset, PartitionList partitionList, Assignments assignments) {
        final String partitionId = assignments.getPartitionId(asset.symbol);
        return partitionList.getName(partitionId);
    }

    private int getPartitionIndex(Asset asset, PartitionList partitionList, Assignments assignments) {
        return partitionList.getIndex(assignments.getPartitionId(asset.symbol));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assets, menu);
    }


    private static class ViewsData {
        public final PartitionList partitionList;
        private final PortfolioAssets portfolioAssets;
        public final Assignments assignments;

        public ViewsData(PartitionList partitionList, PortfolioAssets portfolioAssets, Assignments assignments) {
            this.partitionList = partitionList;
            this.portfolioAssets = portfolioAssets;
            this.assignments = assignments;
        }
    }
}
