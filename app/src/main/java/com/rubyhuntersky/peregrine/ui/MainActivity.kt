package com.rubyhuntersky.peregrine.ui

import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import com.rubyhuntersky.peregrine.R
import com.rubyhuntersky.peregrine.model.AllAccounts
import com.rubyhuntersky.peregrine.utility.toCurrencyDisplayString
import kotlinx.android.synthetic.main.activity_main.*
import rx.Subscription

class MainActivity : BaseActivity() {

    private var titleSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.activity_title)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getCount(): Int {
                return 3
            }

            override fun getItem(position: Int): BaseFragment {
                when (position) {
                    0 -> return NetValueFragment()
                    1 -> return AssetsFragment()
                    2 -> return GroupsFragment()
                    else -> throw IndexOutOfBoundsException("item position: $position")
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                when (position) {
                    0 -> return "Value"
                    1 -> return "Assets"
                    2 -> return "Groups"
                    else -> throw IndexOutOfBoundsException("page-title position: $position")
                }
            }
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        titleSubscription = allAccountsStream.subscribe { toolbar.subtitle = it.toSubtitle() }
    }

    override fun onPause() {
        titleSubscription!!.unsubscribe()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                toolbar.setSubtitle(R.string.processing)
                refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun AllAccounts.toSubtitle() = "${this.netWorth.toCurrencyDisplayString()} \u25f7\u200a${this.relativeArrivalTime}"
}
