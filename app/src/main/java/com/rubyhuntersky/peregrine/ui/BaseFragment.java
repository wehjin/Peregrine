package com.rubyhuntersky.peregrine.ui;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.rubyhuntersky.peregrine.EtradeApi;
import com.rubyhuntersky.peregrine.Storage;

import rx.functions.Action1;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    protected Action1<Throwable> getErrorAction() {
        return getBaseActivity().errorAction;
    }

    protected Storage getStorage() {
        return getBaseActivity().getStorage();
    }

    protected EtradeApi getEtradeApi() {
        return getBaseActivity().getEtradeApi();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

}
