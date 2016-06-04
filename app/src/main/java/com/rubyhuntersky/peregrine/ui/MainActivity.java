package com.rubyhuntersky.peregrine.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rubyhuntersky.peregrine.model.AllAccounts;
import com.rubyhuntersky.peregrine.R;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    private Subscription titleSubscription;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_title);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public BaseFragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new NetValueFragment();
                    case 1:
                        return new AssetsFragment();
                    case 2:
                        return new GroupsFragment();
                    default:
                        return null;
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Value";
                    case 1:
                        return "Assets";
                    case 2:
                        return "Groups";
                    default:
                        return null;
                }
            }
        };
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        titleSubscription = getAllAccountsStream().subscribe(new Action1<AllAccounts>() {
            @Override
            public void call(AllAccounts allAccounts) {
                if (allAccounts == null) {
                    return;
                }

                toolbar.setSubtitle(allAccounts.getRelativeArrivalTime());
            }
        });
    }

    @Override
    protected void onPause() {
        titleSubscription.unsubscribe();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            toolbar.setSubtitle(R.string.processing);
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
