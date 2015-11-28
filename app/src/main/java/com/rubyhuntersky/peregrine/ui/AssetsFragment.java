package com.rubyhuntersky.peregrine.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rubyhuntersky.peregrine.AccountAssets;
import com.rubyhuntersky.peregrine.R;

import java.util.List;

import rx.functions.Action1;

public class AssetsFragment extends BaseFragment {

    private TextView textView;

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textView.setText("");
        getBaseActivity().getAccountAssetsListStream()
                         .subscribe(new Action1<List<AccountAssets>>() {
                             @Override
                             public void call(List<AccountAssets> accountAssetses) {
                                 updateTextView(accountAssetses);
                             }
                         }, getErrorAction());
    }

    @Override
    protected Action1<Throwable> getErrorAction() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error", throwable);
                textView.setText(throwable.getLocalizedMessage());
            }
        };
    }

    private void updateTextView(List<AccountAssets> accountAssetsList) {
        String text;
        if (accountAssetsList == null) {
            text = "No data";
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            for (AccountAssets assetsList : accountAssetsList) {
                stringBuilder.append(assetsList).append("\n\n");
            }
            text = stringBuilder.toString();
        }
        textView.setText(text);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assets, menu);
    }


}
