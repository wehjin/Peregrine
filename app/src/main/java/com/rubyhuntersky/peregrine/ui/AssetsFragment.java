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

import com.rubyhuntersky.peregrine.AccountAssets;
import com.rubyhuntersky.peregrine.Asset;
import com.rubyhuntersky.peregrine.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

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
        View view = inflater.inflate(R.layout.activity_assets, container, false);
        textView = (TextView) view.findViewById(R.id.text);
        listView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().getAccountAssetsListStream()
                         .subscribe(new Action1<List<AccountAssets>>() {
                             @Override
                             public void call(List<AccountAssets> accountAssetses) {
                                 updateViews(accountAssetses);
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

    private void updateViews(List<AccountAssets> accountAssetsList) {
        final List<Asset> assets = getAssets(accountAssetsList);
        if (assets.isEmpty()) {
            showText("No data");
        } else {
            showList(getAssetsBaseAdapter(getActivity(), assets));
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
    private String getAssetsString(List<AccountAssets> accountAssetsList) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (AccountAssets assetsList : accountAssetsList) {
            stringBuilder.append(assetsList).append("\n\n");
        }
        return stringBuilder.toString();
    }

    @NonNull
    private BaseAdapter getAssetsBaseAdapter(final Context context, final List<Asset> assets) {
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
                final TextView endText = (TextView) view.findViewById(R.id.endText);
                startText.setText(asset.symbol);
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
