package com.rubyhuntersky.peregrine.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rubyhuntersky.peregrine.AccountsList;
import com.rubyhuntersky.peregrine.R;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    private Subscription titleSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Fix toolbar text color.  Must follow setSupportActionBar.
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.lightText));
        titleSubscription = getAccountsListStream().subscribe(new Action1<AccountsList>() {
            @Override
            public void call(AccountsList accountsList) {
                updateTitle(accountsList);
            }
        });

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

    private void updateTitle(AccountsList accountsList) {
        if (accountsList == null) {
            return;
        }
        CharSequence title = UiHelper.getRelativeTimeString(accountsList.arrivalDate.getTime());
        setTitle(title);
    }

    @Override
    protected void onDestroy() {
        titleSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
