package com.rubyhuntersky.peregrine;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rubyhuntersky.peregrine.exception.ProductionStorage;

/**
 * @author wehjin
 * @since 11/21/15.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected EtradeApi etradeApi;
    protected Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etradeApi = new EtradeApi(this);
        storage = new ProductionStorage(this);
    }

    public EtradeApi getEtradeApi() {
        return etradeApi;
    }

    public Storage getStorage() {
        return storage;
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
